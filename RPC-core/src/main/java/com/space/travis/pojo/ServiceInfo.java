package com.space.travis.pojo;

import lombok.Data;
import org.springframework.context.annotation.Description;

import java.util.Objects;

/**
 * @ClassName ServiceInfo
 * @Description 存储在zookeeper注册的服务实例信息
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/21
 */
@Data
public class ServiceInfo {
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 版本号
     */
    private String version;
    /**
     * 地址
     */
    private String address;
    /**
     * 端口
     */
    private Integer port;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceInfo that = (ServiceInfo) o;
        return Objects.equals(appName, that.appName) && Objects.equals(serviceName, that.serviceName) && Objects.equals(version, that.version) && Objects.equals(address, that.address) && Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, serviceName, version, address, port);
    }
}
