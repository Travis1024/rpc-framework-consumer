package com.space.travis.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName RpcCuratorProperties
 * @Description curator参数配置
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/2
 */
@Data
public class RpcCuratorProperties {
    /**
     * 定义重试次数
     */
    private Integer retryCount;
    /**
     * 定义间隔时间、以毫秒为单位
     */
    private Integer elapsedTimeMs;
    /**
     * 定义basepath
     */
    private String zookBasePath;
    /**
     * 设置会话超时时间、以毫秒为单位
     */
    private Integer sessionTimeMs;
    /**
     * 设置连接超时时间、以毫秒为单位
     */
    private Integer connectionTimeMs;
}
