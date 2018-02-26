package x.demo.springcloud.microservice.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * rest服务
 */
@RestController
@RequestMapping("/time/v1")
@Slf4j
public class RemoteTimeV1Controller {

    private static final DateTimeFormatter DEFALUT_FORMATER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${server.port}")
    private int port;

    /**
     * 获取时间服务
     *
     * @param format 自定义格式
     * @return 时间
     */
    @GetMapping("/now")
    public ProtocolResult<String> now(@RequestParam(name = "format", required = false) String format) {
        String time;

        log.info("service port [{}] receive client call!", port);
        try {
            if (StringUtils.isEmpty(format)) {
                time = LocalDateTime.now().format(DEFALUT_FORMATER);
            } else {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
                time = LocalDateTime.now().format(dateTimeFormatter);
            }
            return new ProtocolResult<>(0, "success", time);
        } catch (Exception e) {
            return new ProtocolResult<>(-1, e.getMessage());
        }
    }
}
