package x.demo.springcloud.webfront.service.impl.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import x.demo.springcloud.webfront.service.impl.ProtocolResult;

/**
 * 实现 Circuit Breaker 的 FeignClient
 * <p>
 * 特别注意 feign 添加hystrix支持是，会有多个接口实现，spring context 不知道使用哪个进行注入，可以加入 primary = false 在指定
 */
@FeignClient(name = "microservice-time", fallback = TimeV1MirocServiceFallback.class , primary = true)
public interface TimeV1MicroServiceCircuitBreakerClient {

    @RequestMapping(method = RequestMethod.GET, value = "/time/v1/now", consumes = MediaType.APPLICATION_JSON_VALUE)
    ProtocolResult<String> now(@RequestParam(name = "format", required = false) String format);
}

/**
 * fallback方法，必须实现  @FeignClient 注释的接口
 * <p>
 * 特别注意：必须加@Component等注解，仍容器能找到并初始化bean
 */
@Component
class TimeV1MirocServiceFallback implements TimeV1MicroServiceCircuitBreakerClient {
    @Override
    public ProtocolResult<String> now(String format) {
        ProtocolResult<String> fallbackResult = new ProtocolResult<>();
        fallbackResult.setCode(-1);
        fallbackResult.setMessage("fallback");
        fallbackResult.setBody("fallback : " + String.valueOf(System.currentTimeMillis()));
        return fallbackResult;
    }
}
