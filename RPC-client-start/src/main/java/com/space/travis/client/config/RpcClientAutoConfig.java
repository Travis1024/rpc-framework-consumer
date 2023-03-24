package com.space.travis.client.config;

import com.space.travis.client.balancer.FullRoundBalance;
import com.space.travis.client.balancer.RandomBalance;
import com.space.travis.client.discover.ClientDiscoverService;
import com.space.travis.client.discover.ZookDiscoverService;
import com.space.travis.client.processor.RpcClientProcessor;
import com.space.travis.client.transport.NettyRpcClient;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @ClassName RpcClientAutoConfig
 * @Description RPC客户端自动配置
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/2
 */
@Configuration
@Slf4j
public class RpcClientAutoConfig {

    /**
     * 创建rpc客户端属性类，并注入到spring容器中
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    RpcClientProperties rpcClientProperties(Environment environment) {
        BindResult<RpcClientProperties> bind = Binder.get(environment).bind("rpc.client", RpcClientProperties.class);
        return bind.get();
    }

    /**
     * 创建zook客户端curator的配置类，并注入到spring容器中
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    RpcCuratorProperties rpcCuratorProperties(Environment environment) {
        BindResult<RpcCuratorProperties> bind = Binder.get(environment).bind("curator", RpcCuratorProperties.class);
        return bind.get();
    }

    /**
     * 创建并初始化netty客户端
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    NettyRpcClient nettyRpcClient() {
        NettyRpcClient nettyRpcClient = new NettyRpcClient();
        nettyRpcClient.initNetty();
        return nettyRpcClient;
    }


    /**
     * 初始化服务发现对象，并注入到spring容器中
     * @param rpcClientProperties
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({RpcClientProperties.class, RpcCuratorProperties.class})
    ClientDiscoverService clientDiscoverService(@Autowired RpcClientProperties rpcClientProperties, @Autowired RpcCuratorProperties rpcCuratorProperties) {
        Object loadBalance = null;
        switch (rpcClientProperties.getBalance()) {
            case "RandomBalance":
                loadBalance = new RandomBalance();
                break;
            case "FullRoundBalance":
                loadBalance = new FullRoundBalance();
                break;
            default:
                // 如果没有匹配，则返回字符串（其含义为用户自定义负载均衡类的类名）
                loadBalance = rpcClientProperties.getBalance();
        }
        log.info("--------ZookDiscoverService--------");
        return new ZookDiscoverService(rpcClientProperties.getDiscoveryAddr(), loadBalance, rpcCuratorProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcClientProcessor rpcClientProcessor(@Autowired RpcClientProperties rpcClientProperties,
                                                 @Autowired ClientDiscoverService clientDiscoverService,
                                                 @Autowired NettyRpcClient nettyRpcClient){
        return new RpcClientProcessor(rpcClientProperties, clientDiscoverService, nettyRpcClient);
    }
}



