package x.demo.springcloud.webfront.service.impl.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import x.demo.springcloud.webfront.service.impl.ProtocolResult;

/**
 * 集成服务发现的client
 * name：为服务提供者应用名称
 */
@FeignClient(name = "microservice-time")
public interface TimeV1MicroServiceDiscoveryClient {

    @RequestMapping(method = RequestMethod.GET, value = "/time/v1/now", consumes = MediaType.APPLICATION_JSON_VALUE)
    ProtocolResult<String> now(@RequestParam(name = "format", required = false) String format);
}
