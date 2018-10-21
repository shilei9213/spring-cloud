package x.demo.netflix.ribbon;

import java.util.Arrays;
import java.util.List;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.NoOpPing;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;

/**
 * LoadBalancerDemo
 */
public class LoadBalancerDemo {

    public static void main(String[] args) {
        // 负载均衡规则
        IRule rule = new RoundRobinRule();
        // ping 规则
        IPing ping = new NoOpPing();

        // 添加Server 列表
        List<Server> serverList = Arrays.asList(
                new Server("server1"),
                new Server("server2"),
                new Server("server3"),
                new Server("server4"),
                new Server("server5")
        );

        //初始化LoadBalancer
        ILoadBalancer loadBalancer =
                LoadBalancerBuilder
                        .newBuilder()
                        .withRule(rule)
                        .withPing(ping)
                        .buildFixedServerListLoadBalancer(serverList);


        // 简单查看效果
        for (int i = 0; i < 10; i++) {
            System.out.println(loadBalancer.chooseServer(null));
        }
    }
}
