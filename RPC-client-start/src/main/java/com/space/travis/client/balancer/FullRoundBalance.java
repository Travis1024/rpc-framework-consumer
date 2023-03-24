package com.space.travis.client.balancer;

import com.space.travis.exception.NotFoundServiceInstanceException;
import com.space.travis.pojo.ServiceInfo;

import java.util.List;

/**
 * @ClassName FullRoundBalance
 * @Description 以轮询方式进行zookeeper中服务实例的选择
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/21
 */
public class FullRoundBalance implements LoadBalance{

    private static int index = 0;

    @Override
    public ServiceInfo chooseOneInstance(List<ServiceInfo> serviceInfoList) {
        if (serviceInfoList.size() == 0) {
            throw new NotFoundServiceInstanceException("未在zookeeper中找到服务实例，请确认有服务实例已经开启！");
        }
        if (index >= serviceInfoList.size()) {
            index = 0;
        }
        return serviceInfoList.get(index++);
    }
}
