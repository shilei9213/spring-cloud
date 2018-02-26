package x.demo.springcloud.webfront.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import x.demo.springcloud.webfront.service.TimeService;

@Service("timeV1MicroServiceImpl")
public class TimeV1MicroServiceImpl implements TimeService {

    @Value("${timeMisroService.v1.uri}")
    private String timeMicroServiceV1Uri;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 获取当前时间
     *
     * @return 当前时间，格式：yyyy-MM-dd HH:mm:ss
     */
    @Override
    public String now() {
        ProtocolResult<String> result = restTemplate.getForObject(timeMicroServiceV1Uri + "/now", ProtocolResult.class);
        return result.getBody();
    }


}
