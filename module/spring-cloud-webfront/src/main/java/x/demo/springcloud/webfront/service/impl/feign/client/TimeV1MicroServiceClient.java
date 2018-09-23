package x.demo.springcloud.webfront.service.impl.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import x.demo.springcloud.webfront.service.impl.ProtocolResult;

/**
 * 基础feignclient
 */
@FeignClient(name = "microservice-time", url = "${timeMisroService.server}")
public interface TimeV1MicroServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/time/v1/now", consumes = MediaType.APPLICATION_JSON_VALUE)
    ProtocolResult<String> now(@RequestParam(name = "format", required = false) String format);
}
