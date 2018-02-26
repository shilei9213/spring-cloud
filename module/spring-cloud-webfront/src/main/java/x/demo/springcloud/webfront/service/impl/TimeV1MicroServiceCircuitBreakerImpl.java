package x.demo.springcloud.webfront.service.impl;

import javax.annotation.Resource;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;
import x.demo.springcloud.webfront.service.TimeService;
import x.demo.springcloud.webfront.service.impl.feign.TimeV1MicroServiceDiscoveryClient;

@Service("timeV1MicroServiceCircuitBreakerImpl")
public class TimeV1MicroServiceCircuitBreakerImpl implements TimeService {

    @Resource
    private TimeV1MicroServiceDiscoveryClient timeV1MicroServiceDiscoveryClient;

    /**
     * 获取当前时间
     *
     * @return 当前时间，格式：yyyy-MM-dd HH:mm:ss
     */
    @Override
    @HystrixCommand(fallbackMethod = "fallbackNow")
    public String now() {
        ProtocolResult<String> result = timeV1MicroServiceDiscoveryClient.now(null);
        return result.getBody();
    }

    /**
     * 断路器打开时的回调方法
     *
     * @return 当前时间：毫秒数
     */
    public String fallbackNow(){
        return String.valueOf(System.currentTimeMillis());
    }
}
