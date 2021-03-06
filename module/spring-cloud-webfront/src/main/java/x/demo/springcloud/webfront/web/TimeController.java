package x.demo.springcloud.webfront.web;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import x.demo.springcloud.webfront.service.TimeService;

@RestController
@RequestMapping("/time")
public class TimeController {

    @Resource(name = "timeServiceRestClientImpl")
    private TimeService timeService;

    @GetMapping("/now")
    public String now() {
        return timeService.now();
    }
}

