package x.demo.netflix.hystrix;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import rx.Observable;

/**
 * Hystrix demo：
 *
 * @author shilei0907
 */
public class HystrixDemo {

    private Service service = new ServiceCircuitBreakerProxy();

    public static void main(String[] args) throws Exception {
        HystrixDemo demo = new HystrixDemo();

        //请求服务
        for (int i = 0; i < 20; i++) {
            demo.run();
        }

        //等待任务结束
        TimeUnit.MINUTES.sleep(1);
    }

    void run() {
        service.service();
    }

    /**
     * 待监测的服务接口
     */
    interface Service {
        boolean service();
    }

    /**
     * 熔断器代理
     */
    static class ServiceCircuitBreakerProxy extends HystrixCommand<Boolean> implements Service {

        /**
         * 配置HystrixCommand：
         * 滑动窗口：1000 毫秒
         * 桶数：1
         * --- 则每个统计周期1000毫秒
         * 超时时间：100毫秒
         * 熔断器打开错误率：50%
         * --- 则一个滑动窗口内全部超时约执行10次（有其他是爱你消耗），预计执行8~9次run() 熔断器打开，之后请求直接进入getFallback
         */
        public ServiceCircuitBreakerProxy() {
            super(Setter
                    /*
                    一般情况相同业务功能会使用相同的CommandGroupKey。对CommandKey分组，进行逻辑隔离。相同CommandGroupKey会使用同一个线程池或者信号量
                     */
                    .withGroupKey(HystrixCommandGroupKey.Factory.asKey(ServiceCircuitBreakerProxy.class.getSimpleName()))
                    /*
                    一般同一监控服务使用相同的CommandKey，目的把HystrixCommand，HystrixCircuitBreaker，HytrixCommandMerics
                    以及其他相关对象关联在一起，形成一个原子组。采用原生接口的话，默认值为类名；采用注解形式的话，默认值为方法名
                     */
                    .andCommandKey(HystrixCommandKey.Factory.asKey("HelloWorld"))
                    .andCommandPropertiesDefaults(
                            HystrixCommandProperties.Setter()
                                    /*
                                    隔离级别，默认线程
                                     */
                                    .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                                    /*
                                    线程执行超时时间，默认1000，一般选择所服务tp99的时间
                                     */
                                    .withExecutionTimeoutInMilliseconds(50)
                                    /*
                                    默认20；一个滑动窗口内“触发熔断”要达到的最小访问次数。低于该次数，技术错误率达到，也不会触发熔断操作，用于测试压力是否满足要求。
                                     */
                                    .withCircuitBreakerRequestVolumeThreshold(1)
                                    /*
                                    一个窗口内“触发熔断”错误率。满足则进入熔断状态，快速失效。
                                     */
                                    .withCircuitBreakerErrorThresholdPercentage(50)
                                    /*
                                    默认 5000（即5s）；断路器打开后过多久调用时间服务进行重试。
                                     */
                                    .withCircuitBreakerSleepWindowInMilliseconds(10000)
                    )
                    .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                            /*
                            线程池大小，默认10
                             */
                            .withCoreSize(1)
                            /*
                            任务队列大小，使用BlockingQueue，默认-1
                             */
                            .withMaxQueueSize(-1)
                            /*
                            默认1000（即10）；设置统计的滑动窗口大小，毫秒值。每一个滑动窗口是决策周期，用于CircuitBreaker计算错误率，做状态改变。
                             */
                            .withMetricsRollingStatisticalWindowInMilliseconds(1000)
                            /*
                            默认10；设置一个滑动窗口内桶的数量，一个bucket的时间周期=timeInMilliseconds/numBuckets,
                            是统计的最小时间单元，独立计数。Hystrix的滑动窗口按照一个bucket的时间周期向前滑动，合并最近的n个bucket的统计数据，即为一个时间窗口，计算错误率，改变状态。
                             */
                            .withMetricsRollingStatisticalWindowBuckets(1)
                    )
            );
        }

        /**
         * 代理实际业务
         */
        @Override
        public boolean service() {
            return doExecute();

        }

        /**
         * (1)
         * 同步方式调用：以同步堵塞方式执行的run()。
         * 调用execute()后，hystrix先创建一个新线程运行run()，接着调用程序要在execute()调用处一直堵塞着，直到run()运行完成
         *
         * @return 结果
         */
        private boolean doExecute() {
            return new ServiceCircuitBreakerProxy().execute();
        }

        /**
         * (2)
         * queue()：以异步非堵塞方式执行的run()。
         * 调用queue()就直接返回一个Future对象，同时hystrix创建一个新线程运行run()，调用程序通过Future.get()拿到run()的返回结果，而Future.get()是堵塞执行的
         *
         * @return 结果
         */
        private boolean doQueue() throws ExecutionException, InterruptedException {
            Future<Boolean> future = new ServiceCircuitBreakerProxy().queue();
            return future.get();
        }

        /**
         * (3)
         * 执行 热Observable：observe()——创建后马上执行Command，生成可观察事件。但观察者注册完后，可能从中间消费事件，造成一定量事件无法观察到。
         * 事件注册前执行run()/construct()。
         * 第一步是事件注册前，先调用observe()自动触发执行run()/construct().（如果继承的是HystrixCommand，hystrix将创建新线程非堵塞执行run()
         * ；如果继承的是HystrixObservableCommand，将以调用程序线程堵塞执行construct()
         * 第二步是从observe()返回后调用程序调用subscribe()完成事件注册，如果run()/construct()执行成功则触发onNext()和onCompleted()，如果执行异常则触发onError()
         *
         * @return 结果
         */
        private Observable<Boolean> doObserve() {
            return new ServiceCircuitBreakerProxy().observe();

        }

        /**
         * （4）
         * 执行：冷Observable：toObservable()—— Observable 一直等待，直到有观察者订阅他才开始发送数据，因此观察者可以确保收到整个数据序列。
         * 事件注册后执行run()/construct()。
         * 第一步是事件注册前，一调用toObservable()就直接返回一个Observable<String>对象，
         * 第二步调用subscribe() 完成事件注册后自动触发执行run()/construct()（如果继承的是HystrixCommand，hystrix将创建新线程非堵塞执行run()，调用程序不必等待run()
         * ；如果继承的是HystrixObservableCommand，将以调用程序线程堵塞执行construct()，调用程序等待construct()执行完才能继续往下走），如果run()/construct()
         * 执行成功则触发onNext()和onCompleted()，如果执行异常则触发onError()
         *
         * @return 结果
         */
        private Observable<Boolean> toToObservable() {
            return new ServiceCircuitBreakerProxy().toObservable();
        }


        @Override
        protected Boolean run() throws Exception {
            System.out.println("Thread " + Thread.currentThread().getId() + " ：run()");
            //模拟超时
            TimeUnit.SECONDS.sleep(1);
            return true;
        }

        @Override
        protected Boolean getFallback() {
            System.out.println("Thread " + Thread.currentThread().getId() + " ：getFallback()");
            return false;
        }


    }
}