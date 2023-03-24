package com.space.travis.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName RpcClientProperties
 * @Description rpc客户端配置属性
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/2
 */
@Data
public class RpcClientProperties {
    /**
     * 负载均衡策略
     */
    private String balance;
    /**
     * 序列化
     */
    private String serialization;
    /**
     * 服务发现中心地址（zookeeper）
     */
    private String discoveryAddr;
    /**
     * 服务调用超时
     */
    private Integer timeout;
}
