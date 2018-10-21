package x.demo.springcloud.webfront.service.impl.ribbon;

import java.util.Arrays;
import java.util.List;

import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.NoOpPing;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.springframework.context.annotation.Bean;

/**
 * Ribbon 负载均衡配置
 * <p>
 * 特别注意：
 * 这里是针对 name = "microservice-time" 的服务进行的配置，所以不能 添加 @Configuration ，否则接入容器，会全局生效
 *
 * @author shilei0907
 * @version 1.0, 2018/9/25
 */
//@RibbonClient(name = "microservice-time", configuration = TimeMicroServiceConfiguration.class)
public class TimeMicroServiceConfiguration {

    @Bean
    public IRule rule() {
        return new RandomRule();
    }

    @Bean
    public IPing ping() {
        return new NoOpPing();
    }

    @Bean
    public ServerList<Server> serverList() {
        List<Server> servers = Arrays.asList(new Server("127.0.0.1", 10001), new Server("127.0.0.1", 10002));

        ServerList<Server> serverList = new ServerList<Server>() {
            @Override
            public List<Server> getInitialListOfServers() {
                return servers;
            }

            @Override
            public List<Server> getUpdatedListOfServers() {
                return servers;
            }
        };

        return serverList;
    }
}
