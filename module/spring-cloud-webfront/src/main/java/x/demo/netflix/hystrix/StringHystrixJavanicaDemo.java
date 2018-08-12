package x.demo.netflix.hystrix;

import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/**
 * StringHystrixJavanicaDemo
 *
 * @author shilei0907
 * @version 1.0, 2018/6/6
 */
@EnableAspectJAutoProxy
public class StringHystrixJavanicaDemo {

    @Resource
    private Service service;


    public static void main(String[] args) throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();) {
            context.register(StringHystrixJavanicaDemo.class);
            context.refresh();

            StringHystrixJavanicaDemo demo = context.getBean(StringHystrixJavanicaDemo.class);
            for (int i = 0; i < 20; i++) {
                demo.run();
            }
        }
    }

    void run() throws InterruptedException {
        service.doService();
    }

    @Component
    static class Service {

        /**
         * 被熔断管理的方法
         *
         * @return 是否调用成功
         */
        @HystrixCommand(
                groupKey = "StringHystrixJavanicaDemo",
                commandKey = "HelloWorld",
                defaultFallback = "doFallack",
                commandProperties = {
                        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "100"),

                        @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "1"),
                        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50"),
                        @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000")
                },
                threadPoolProperties = {
                        @HystrixProperty(name = "coreSize", value = "1"),
                        @HystrixProperty(name = "maxQueueSize", value = "-1"),
                        @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1000"),
                        @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "1")})

        public boolean doService() throws InterruptedException {
            System.out.println("Thread " + Thread.currentThread().getId() + " ：run()");
            //模拟超时
            TimeUnit.SECONDS.sleep(1);
            return true;
        }


        /**
         * 熔断器开启后，控制默认返回
         *
         * @return 期望的默认值
         */
        public boolean doFallack() {
            System.out.println("Thread " + Thread.currentThread().getId() + " ：getFallback()");
            return false;
        }
    }

    @Configuration
    static class HystrixConfiguration {

        /**
         * 处理代理类
         *
         * @return 代理
         */
        @Bean
        public HystrixCommandAspect hystrixCommandAspect() {
            return new HystrixCommandAspect();
        }
    }


}
