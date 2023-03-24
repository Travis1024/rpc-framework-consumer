package com.space.travis.client.balancer;

import com.space.travis.exception.NotFoundServiceInstanceException;
import com.space.travis.pojo.ServiceInfo;

import java.util.List;
import java.util.Random;

/**
 * @ClassName RandomBalance
 * @Description 随机选择zookeeper中的服务实例
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/21
 */
public class RandomBalance implements LoadBalance{

    private static Random random = new Random();


    /**
     * @MethodName chooseOneInstance
     * @Description 随机返回zookeeper中的服务实例
     * @Author travis-wei
     * @Data 2023/2/21
     * @param serviceInfoList
     * @Return com.space.travis.pojo.ServiceInfo
     **/
    @Override
    public ServiceInfo chooseOneInstance(List<ServiceInfo> serviceInfoList) {
        if (serviceInfoList.size() == 0) {
            throw new NotFoundServiceInstanceException("未在zookeeper中找到服务实例，请确认有服务实例已经开启！");
        }
        return serviceInfoList.get(random.nextInt(serviceInfoList.size()));
    }
}
