package com.space.travis.client.processor;


import com.space.travis.client.annotation.RpcAutowired;
import com.space.travis.client.config.RpcClientProperties;
import com.space.travis.client.discover.ClientDiscoverService;
import com.space.travis.client.proxy.ClientProxyFactory;
import com.space.travis.client.transport.NettyRpcClient;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @ClassName RpcClientProcessor
 * @Description 实现bean后置处理器
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/2
 */
@Slf4j
public class RpcClientProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {

    /**
     * 通过ApplicationContextAware获取上下文对象，并将上下文独对象赋值给applicationContext
     */
    private ApplicationContext applicationContext;

    private RpcClientProperties rpcClientProperties;
    private ClientDiscoverService clientDiscoverService;
    private NettyRpcClient nettyRpcClient;

    public RpcClientProcessor(RpcClientProperties rpcClientProperties, ClientDiscoverService clientDiscoverService, NettyRpcClient nettyRpcClient) {
        this.rpcClientProperties = rpcClientProperties;
        this.clientDiscoverService = clientDiscoverService;
        this.nettyRpcClient = nettyRpcClient;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            // 获取bean类名，目标为（SayController）
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
                // 获取SayController类实例，类加载器传入null则会使用默认的类加载器
                Class<?> aClass = ClassUtils.resolveClassName(beanClassName,null);
                // 获取SayController类实例的属性
                ReflectionUtils.doWithFields(aClass, field -> {
                    // 判断当前属性（成员变量）是否被RpcAutowired注解
                    RpcAutowired annotation = AnnotationUtils.getAnnotation(field, RpcAutowired.class);
                    if (annotation != null) {
                        // 获取SayController类的bean
                        Object bean = applicationContext.getBean(aClass);
                        // 作用为让我们在用反射时访问私有变量（private FirstSayService firstSayService ｜ private SecondSayService secondSayService）
                        field.setAccessible(true);
                        /**
                         * void setField(Field field, Object target, Object value)
                         * 可以设置 target 对象的 field 属性值，值为 value。
                         */
                        // field.getType()-类对象信息：FirstSayService.class 和 SecondSayService.class  |  annotation.version()-注解版本：1.0
                        ClientProxyFactory clientProxyFactory = new ClientProxyFactory(rpcClientProperties, clientDiscoverService, nettyRpcClient, field.getType(), annotation.version());
                        Object proxy = clientProxyFactory.getProxy();
                        // 获取代理对象FirstSayService.class 和 SecondSayService.class注解版本的代理对象，并将代理对象赋值给（private FirstSayService firstSayService ｜ private SecondSayService secondSayService）
                        ReflectionUtils.setField(field, bean, proxy);
                    }
                });
            }
        }
    }

    /**
     * @MethodName setApplicationContext
     * @Description 获取上下文对象
     * @Author travis-wei
     * @Data 2023/3/2
     * @param applicationContext
     * @Return void
     **/
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
