package x.demo.springcloud.webfront.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import x.demo.springcloud.webfront.service.TimeService;
import x.demo.springcloud.webfront.service.impl.feign.TimeV1MicroServiceCircuitBreakerClient;

@Service("timeV1FeignImpl")
public class TimeV1FeignImpl implements TimeService {

    @Resource
    private TimeV1MicroServiceCircuitBreakerClient timeV1MicroServiceCircuitBreakerClient;

    /**
     * 获取当前时间
     *
     * @return 当前时间，格式：yyyy-MM-dd HH:mm:ss
     */
    @Override
    public String now() {
        ProtocolResult<String> result = timeV1MicroServiceCircuitBreakerClient.now(null);
        return result.getBody();
    }
}
