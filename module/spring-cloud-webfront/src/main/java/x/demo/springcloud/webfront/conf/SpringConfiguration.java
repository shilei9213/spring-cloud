package x.demo.springcloud.webfront.conf;

import feign.Feign;
import feign.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import x.demo.springcloud.webfront.service.impl.feign.TimeV1MicroServiceClient;

@Configuration
public class SpringConfiguration {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Value("${timeMisroService.server}")
    private String timeMisroServiceServer;

    @Bean
    public TimeV1MicroServiceClient timeV1MicroServiceClient() {
        return Feign.builder()
                .contract(new SpringMvcContract())
                .options(new Request.Options(5000, 5000))
                .target(TimeV1MicroServiceClient.class, timeMisroServiceServer);
    }

}
