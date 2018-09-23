package x.demo.springcloud.webfront.service.impl.feign;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import x.demo.springcloud.webfront.service.TimeService;
import x.demo.springcloud.webfront.service.impl.ProtocolResult;
import x.demo.springcloud.webfront.service.impl.feign.client.TimeV1MicroServiceWithHystrixClient;

@Service("timeServiceFeignImpl")
public class TimeServiceFeignImpl implements TimeService {

    @Resource
    private TimeV1MicroServiceWithHystrixClient timeV1MicroServiceWithHystrixClient;

    /**
     * 获取当前时间
     *
     * @return 当前时间，格式：yyyy-MM-dd HH:mm:ss
     */
    @Override
    public String now() {
        ProtocolResult<String> result = timeV1MicroServiceWithHystrixClient.now(null);
        return result.getBody();
    }
}
