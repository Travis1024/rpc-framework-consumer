package com.space.travis.loadbalance;

import com.space.travis.client.balancer.LoadBalance;
import com.space.travis.exception.NotFoundServiceInstanceException;
import com.space.travis.pojo.ServiceInfo;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @ClassName FirstLoadBalance
 * @Description 负载均衡策略（第一服务策略）
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/21
 */
@Service
public class FirstLoadBalance implements LoadBalance {
    @Override
    public ServiceInfo chooseOneInstance(List<ServiceInfo> serviceInfoList) {
        if (CollectionUtils.isEmpty(serviceInfoList)) {
            throw new NotFoundServiceInstanceException("未在zookeeper中找到服务实例，请确认有服务实例已经开启！");
        }
        return serviceInfoList.get(0);
    }
}
