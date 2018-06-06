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
                            .withCoreSize(1)
                            .withMaxQueueSize(1)
                            // 设置统计的时间窗口值的，毫秒值，circuit break 的打开会根据1个rolling window的统计来计算。若rolling window被设为10000毫秒，则rolling
                            // window会被分成n个buckets，每个bucket包含success，failure，timeout，rejection的次数的统计信息。默认10000
                            .withMetricsRollingStatisticalWindowInMilliseconds(10000)
                            // 设置一个rolling window被划分的数量，若numBuckets＝10，rolling window＝10000，那么一个bucket的时间即1秒。必须符合rolling window %
                            // numberBuckets == 0。默认10
                            .withMetricsRollingStatisticalWindowBuckets(1)
                    )
                    .andCommandPropertiesDefaults(
                            HystrixCommandProperties.Setter()
                                    // 设置一个窗口内的请求数，当在该窗口内(即时间内)请求数达到了该值，则断路器会被打开,默认20 , 好像1 不好使
                                    .withCircuitBreakerRequestVolumeThreshold(1)
                                    // 超时时间，默认 1000
                                    .withExecutionTimeoutInMilliseconds(10)
                    ));
        }

        @Override
        public boolean service() {
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