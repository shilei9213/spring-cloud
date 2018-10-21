package x.demo.netflix.ribbon;

import java.util.Arrays;
import java.util.List;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.DummyPing;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;

/**
 * RandomRule Demo
 *
 */
public class RandomRuleDemo {

    public static void main(String[] args) {
        List<Server> servers = Arrays.asList(new Server("1"), new Server("2"), new Server("3"));

        BaseLoadBalancer loadBalancer = new BaseLoadBalancer(new DummyPing(), new RoundRobinRule());
        loadBalancer.setServersList(servers);
        loadBalancer.setPingInterval(1);
        loadBalancer.setMaxTotalPingTime(10);

        for (int i = 0; i < 10; i++) {
            System.out.println(loadBalancer.chooseServer());
        }

        System.out.println(loadBalancer.getLoadBalancerStats());
    }

}
