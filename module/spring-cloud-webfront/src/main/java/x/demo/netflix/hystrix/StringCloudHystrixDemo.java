package x.demo.netflix.hystrix;

import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import lombok.SneakyThrows;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/**
 * 熔断器使用
 *
 * @author shilei0907
 * @version 1.0, 2018/6/6
 */
//启动代理
@EnableAspectJAutoProxy
public class StringCloudHystrixDemo {

    @Resource
    private Service service;


    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();) {
            context.register(StringCloudHystrixDemo.class);
            context.refresh();

            StringCloudHystrixDemo demo = context.getBean(StringCloudHystrixDemo.class);
            for (int i = 0; i < 100; i++) {
                demo.run();
            }
        }
    }

    public void run() {
        service.doService();
    }

    @Component
    static class Service {

        /**
         * 被熔断管理的方法
         *
         * @return 是否调用成功
         */
        @SneakyThrows
        // @HystrixCommand由名为“javanica”的Netflix contrib库提供 。Spring Cloud在连接到Hystrix断路器的代理中使用该注释自动包装Spring bean。断路器计算何时打开和关闭电路，以及在发生故障时应该做什么
        @HystrixCommand(defaultFallback = "defalutDoService",
                commandProperties = {
                        //指定多久超时，单位毫秒。超时进fallback
                        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "100"),
                        //判断熔断的最少请求数，默认是10；只有在一个统计窗口内处理的请求数量达到这个阈值，才会进行熔断与否的判断
                        @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "5"),
                        //判断熔断的阈值，默认值50，表示在一个统计窗口内有50%的请求处理失败，会触发熔断
                        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "10")
                })
        public boolean doService() {
            System.out.println("doService");
            TimeUnit.SECONDS.sleep(1);
            return true;
        }


        /**
         * 熔断器开启后，控制默认返回
         *
         * @return 期望的默认值
         */
        public boolean defalutDoService() {
            System.out.println("defaultDoService");
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
