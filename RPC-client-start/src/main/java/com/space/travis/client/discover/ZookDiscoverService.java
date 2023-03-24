package com.space.travis.client.discover;

import com.space.travis.client.balancer.LoadBalance;
import com.space.travis.client.balancer.RandomBalance;
import com.space.travis.client.config.RpcCuratorProperties;
import com.space.travis.pojo.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @ClassName ZookDiscoverService
 * @Description 服务发现接口实现
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/2
 */
@Slf4j
public class ZookDiscoverService implements ClientDiscoverService, ApplicationContextAware {


    private RpcCuratorProperties rpcCuratorProperties;

    private ServiceDiscovery<ServiceInfo> serviceDiscovery;

    private LoadBalance loadBalance;

    private ApplicationContext applicationContext;


    public ZookDiscoverService(String discoveryAddr, Object loadBalance, RpcCuratorProperties rpcCuratorProperties) {
        this.rpcCuratorProperties = rpcCuratorProperties;

        // 判断用户是否对负载均衡策略进行了自定义
        if (loadBalance instanceof String) {
            this.loadBalance = getCustomLoadBalance(String.valueOf(loadBalance));
        } else {
            this.loadBalance = (LoadBalance) loadBalance;
        }

        // 初始化、创建、运行curator客户端
        ExponentialBackoffRetry exponentialBackoffRetry = new ExponentialBackoffRetry(rpcCuratorProperties.getElapsedTimeMs(), rpcCuratorProperties.getRetryCount());
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(discoveryAddr, exponentialBackoffRetry);
        curatorFramework.start();

        // 初始化、创建、运行 《服务发现构造器》
        try {
            JsonInstanceSerializer<ServiceInfo> serviceInfoJsonInstanceSerializer = new JsonInstanceSerializer<>(ServiceInfo.class);
            // 使用ServiceDiscovertBuilder对ServiceDiscovery进行创建
            serviceDiscovery = ServiceDiscoveryBuilder
                    .builder(ServiceInfo.class)
                    .client(curatorFramework)
                    .serializer(serviceInfoJsonInstanceSerializer)
                    .basePath(rpcCuratorProperties.getZookBasePath())
                    .build();
            serviceDiscovery.start();
        } catch (Exception e) {
            log.error("错误：Client-ZookRegisterServer构造函数，创建服务发现构造器时出错！");
            e.printStackTrace();
        }
    }

    /**
     * @MethodName getCustomLoadBalance
     * @Description 处理用户自定义的负载均衡策略
     * @Author travis-wei
     * @Data 2023/3/3
     * @param customLoadBalance
     * @Return com.space.travis.balancer.LoadBalance
     **/
    private LoadBalance getCustomLoadBalance(String customLoadBalance) {
        try {
            // 获取自定义负载均衡类的类实例，类加载器传入null则会使用默认的类加载器
            Class<?> aClass = ClassUtils.resolveClassName(customLoadBalance, null);
            // 获取自定义负载均衡类的bean，并强制转成LoadBalance，返回
            return  (LoadBalance) applicationContext.getBean(aClass);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("错误：未能解析自定义负载均衡类，或解析异常！");
            e.printStackTrace();
            log.info("------开启默认负载均衡策略 (RandomBalance)------");
            return new RandomBalance();
        }
    }

    /**
     * @MethodName discovery
     * @Description 根据负载均衡策略对服务实例进行选择
     * @Author travis-wei
     * @Data 2023/3/3
     * @param serviceName
     * @Return com.space.travis.pojo.ServiceInfo
     **/
    @Override
    public ServiceInfo discovery(String serviceName) {
        try {
            Collection<ServiceInstance<ServiceInfo>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
            if (!CollectionUtils.isEmpty(serviceInstances)) {
                return loadBalance.chooseOneInstance(serviceInstances
                        .stream()
                        .map(ServiceInstance::getPayload)
                        .collect(Collectors.toList()));
            }
            return null;
        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
