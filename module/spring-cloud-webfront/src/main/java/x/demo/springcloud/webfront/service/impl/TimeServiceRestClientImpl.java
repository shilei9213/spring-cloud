package x.demo.springcloud.webfront.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import x.demo.springcloud.webfront.service.TimeService;

@Service
public class TimeServiceRestClientImpl implements TimeService {

    @Value("${timeMisroService.v1.uri}")
    private String timeMicroServiceV1Uri;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取当前时间
     *
     * @return 当前时间，格式：yyyy-MM-dd HH:mm:ss
     */
    @Override
    public String now() {
        String url = timeMicroServiceV1Uri + "/now";
        ProtocolResult<String> result = restTemplate.getForObject(url, ProtocolResult.class);
        return result.getBody();
    }
}
