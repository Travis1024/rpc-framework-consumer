package com.space.travis.client.balancer;

import com.space.travis.pojo.ServiceInfo;

import java.util.List;

/**
 * @ClassName LoadBalance
 * @Description 负载均衡接口
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/21
 */
public interface LoadBalance {
    /**
     * @MethodName chooseOneInstance
     * @Description 根据负载均衡策略，在注册到zookeeper中的服务实例中选择一个实例
     * @Author travis-wei
     * @Data 2023/2/21
     * @param serviceInfoList
     * @Return com.space.travis.pojo.ServiceInfo
     **/
    ServiceInfo chooseOneInstance(List<ServiceInfo> serviceInfoList);

}
