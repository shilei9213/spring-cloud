package x.demo.springcloud.microservice.controller;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class RemoteTimeV1ControllerIT {

    @Resource
    private TestRestTemplate restTemplate;


    @Test
    public void testNowNoFormat() throws Exception {
        ProtocolResult<String> result = restTemplate.getForObject("/time/v1/now", ProtocolResult.class);
        assertThat(result.getCode()).isEqualTo(0);
        log.info("result : " + result);
    }

    @Test
    public void testNowNoFormatYYYY() throws Exception {
        ProtocolResult<String> result = restTemplate.getForObject("/time/v1/now?format=yyyy", ProtocolResult.class);
        assertThat(result.getCode()).isEqualTo(0);
        log.info("result : " + result);
    }

} 
