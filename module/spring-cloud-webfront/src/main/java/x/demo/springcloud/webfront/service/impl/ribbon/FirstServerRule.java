package x.demo.springcloud.webfront.service.impl.ribbon;

import java.util.List;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.Server;

/**
 * 只请求第一台server
 *
 * @author shilei0907
 * @version 1.0, 2018/9/25
 */
public class FirstServerRule extends AbstractLoadBalancerRule {

    @Override
    public Server choose(Object key) {
        List<Server> servers = getLoadBalancer().getReachableServers();
        return servers.get(0);
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {

    }
}
