package x.demo.netflix.hystrix;

import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

/**
 * Hystrix demo
 *
 * @author shilei0907
 * @version 1.0, 2018/6/6
 */
public class HystrixDemo {

    private Service service = new ServiceCircuitBreakerProxy();

    public static void main(String[] args) {
        HystrixDemo demo = new HystrixDemo();

        for (int i = 0; i < 100; i++) {
            demo.run();
        }
    }

    public void run() {
        service.service();
    }

    /**
     * 测试接口
     */
    interface Service {
        boolean service();
    }

    /**
     * 熔断器代理
     */
    static class ServiceCircuitBreakerProxy extends HystrixCommand<Boolean> implements Service {

        public ServiceCircuitBreakerProxy() {
            //setting ： https://github.com/Netflix/Hystrix/wiki/Configuration
            super(Setter
                    .withGroupKey(HystrixCommandGroupKey.Factory.asKey(ServiceCircuitBreakerProxy.class.getSimpleName()))
                    //设置线程池相关参数
                    .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
//                            .withCoreSize(1)
//                            .withMaxQueueSize(1)
                                    // 设置统计的时间窗口值的，毫秒值，circuit break 的打开会根据1个rolling window的统计来计算。若rolling window被设为10000毫秒，则rolling
                                    // window会被分成n个buckets，每个bucket包含success，failure，timeout，rejection的次数的统计信息。默认10000
                                    .withMetricsRollingStatisticalWindowInMilliseconds(1000)
                                    // 设置一个rolling window被划分的数量，若numBuckets＝10，rolling window＝10000，那么一个bucket的时间即1秒。必须符合rolling window %
                                    // numberBuckets == 0。默认10
                                    .withMetricsRollingStatisticalWindowBuckets(1)

                    )
                    .andCommandPropertiesDefaults(
                            //https://www.cnblogs.com/jabnih/p/9013197.html HystrixCircuitBreaker
                            //  断路器打开条件，打开后会断路所有请求
                            //  （1）断路器访问量达到阈值：HystrixCommandProperties.circuitBreakerRequestVolumeThreshold()
                            //  （2）错误百分比超过所设置错误百分比阈值：HystrixCommandProperties.circuitBreakerErrorThresholdPercentage()
                            HystrixCommandProperties.Setter()
                                    //某个时间窗口内访问量, 超过这个量才会触发，时间窗口，withMetricsRollingStatisticalWindowInMilliseconds,即使全失败了也不触发
                                    .withCircuitBreakerRequestVolumeThreshold(1)
                                    //一个时间窗口内错误百分比阈值：例如时间窗口withMetricsRollingStatisticalWindowInMilliseconds ： 1s，错误率百分之一，则下一秒熔断
                                    .withCircuitBreakerErrorThresholdPercentage(1)
                                    //断路器重试时间间隔，该时间后会触发重试，看是否进入关闭状态
                                    .withCircuitBreakerSleepWindowInMilliseconds(10000)
                                    // 超时时间，默认 1000,改时间后会认为超时
                                    .withExecutionTimeoutInMilliseconds(50)
                                    .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)

                    ));
        }

        @Override
        public boolean service() {
            // 同步方式调用：以同步堵塞方式执行run()。以demo为例，调用execute()后，hystrix先创建一个新线程运行run()，接着调用程序要在execute()调用处一直堵塞着，直到run()运行完成
            // 其他方式：
            // (1) queue()：以异步非堵塞方式执行run()。以demo为例，一调用queue()就直接返回一个Future对象，同时hystrix创建一个新线程运行run()，调用程序通过Future.get()拿到run()
            // 的返回结果，而Future.get()是堵塞执行的
            // (2) observe()：事件注册前执行run()/construct()。以demo为例，第一步是事件注册前，先调用observe()自动触发执行run()/construct()
            // （如果继承的是HystrixCommand，hystrix将创建新线程非堵塞执行run()；如果继承的是HystrixObservableCommand，将以调用程序线程堵塞执行construct()），第二步是从observe()
            // 返回后调用程序调用subscribe()完成事件注册，如果run()/construct()执行成功则触发onNext()和onCompleted()，如果执行异常则触发onError()
            //（3）toObservable()：事件注册后执行run()/construct()。以demo为例，第一步是事件注册前，一调用toObservable()就直接返回一个Observable<String>对象，第二步调用subscribe()
            // 完成事件注册后自动触发执行run()/construct()（如果继承的是HystrixCommand，hystrix将创建新线程非堵塞执行run()，调用程序不必等待run()
            // ；如果继承的是HystrixObservableCommand，将以调用程序线程堵塞执行construct()，调用程序等待construct()执行完才能继续往下走），如果run()/construct()执行成功则触发onNext()
            // 和onCompleted()，如果执行异常则触发onError()

            return new ServiceCircuitBreakerProxy().execute();

        }

        @Override
        protected Boolean run() throws Exception {
            System.out.println("----------------------------- run");
            TimeUnit.SECONDS.sleep(1);
            return true;
        }

        @Override
        protected Boolean getFallback() {
            System.out.println("----------------------------- fallback");
            return false;
        }
    }
}