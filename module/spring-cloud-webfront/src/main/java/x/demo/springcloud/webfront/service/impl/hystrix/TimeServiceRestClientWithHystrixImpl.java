package x.demo.springcloud.webfront.service.impl.hystrix;

import javax.annotation.Resource;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import x.demo.springcloud.webfront.service.TimeService;
import x.demo.springcloud.webfront.service.impl.ProtocolResult;

/**
 * Hystrix 熔断实现
 *
 * @author shilei0907
 * @version 1.0, 2018/9/23
 */
@Service
public class TimeServiceRestClientWithHystrixImpl implements TimeService {


    @Value("${timeMisroService.v1.uri}")
    private String timeMicroServiceV1Uri;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 获取当前时间
     *
     * @return 当前时间，格式：yyyy-MM-dd HH:mm:ss
     */
    @HystrixCommand(
            groupKey = "microservice-time",
            commandKey = "microservice-time.now",
            fallbackMethod = "fallbackNow")
    @Override
    public String now() {
        ProtocolResult<String> result = restTemplate.getForObject(timeMicroServiceV1Uri + "/now", ProtocolResult.class);
        return result.getBody();
    }

    /**
     * 断路器打开时的回调方法
     *
     * @return 当前时间：毫秒数
     */
    public String fallbackNow() {
        return "fallback：" + String.valueOf(System.currentTimeMillis());
    }
}
