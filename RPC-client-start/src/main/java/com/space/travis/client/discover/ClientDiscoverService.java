package com.space.travis.client.discover;

import com.space.travis.pojo.ServiceInfo;
import org.springframework.stereotype.Service;

/**
 * @ClassName ClientDiscoverService
 * @Description 发现服务接口
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/2
 */
@Service
public interface ClientDiscoverService {
    /**
     * 在zookeeper注册中心发现服务
     * @param serviceName
     * @return
     */
    ServiceInfo discovery(String serviceName);
}
