package com.space.travis.client.proxy;

import com.space.travis.client.config.RpcClientProperties;
import com.space.travis.client.discover.ClientDiscoverService;
import com.space.travis.client.transport.NettyRpcClient;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName ClientProxyFactory
 * @Description 客户端代理工厂
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/2
 */
public class ClientProxyFactory {

    // map缓存，(key:类对象, value:代理对象)
    private static Map<Class<?>, Object> mapCache = new HashMap<>();

    private RpcClientProperties rpcClientProperties;
    private ClientDiscoverService clientDiscoverService;
    private NettyRpcClient nettyRpcClient;
    private Class<?> classObject;
    private String version;


    /**
     * @MethodName ClientProxyFactory
     * @Description 构造方法，初始化成员变量
     * @Author travis-wei
     * @Data 2023/3/24
     * @param rpcClientProperties
     * @param clientDiscoverService
     * @param nettyRpcClient
     * @param classObject   类对象信息：FirstSayService.class or SecondSayService.class
     * @param version   注解版本：1.0
     * @Return
     **/
    public ClientProxyFactory(RpcClientProperties rpcClientProperties, ClientDiscoverService clientDiscoverService, NettyRpcClient nettyRpcClient, Class<?> classObject, String version) {
        this.rpcClientProperties = rpcClientProperties;
        this.clientDiscoverService = clientDiscoverService;
        this.nettyRpcClient = nettyRpcClient;
        this.classObject = classObject;
        this.version = version;
    }

    /**
     * @MethodName getProxy
     * @Description 获取代理对象
     * @Author travis-wei
     * @Data 2023/3/2
     * @param
     * @Return java.lang.Object
     **/
    public <T> T getProxy() {
        if (mapCache.containsKey(classObject)) {
            return (T) mapCache.get(classObject);
        }

        // 获取类对应的ClassLoader
        ClassLoader classLoader = classObject.getClassLoader();

        // 获取类所有接口的class
        // Class<?>[] interfaces = classObject.getInterfaces();

        // 创建代理类的调用请求处理器，处理所有代理对象上的方法调用
        ClientProxyInvocationHandler proxyHandler = new ClientProxyInvocationHandler(rpcClientProperties, clientDiscoverService, nettyRpcClient, classObject, version);

        /**
         * ---创建代理对象（在这个过程中）
         * （a）JDK会通过根据传入的参数信息动态地在内存中创建和.class文件等同的字节码
         * （b）然后根据相应的字节码转换成对应的class
         * （c）然后调用newInstance()创建代理实例
         */
        Object proxyInstance = Proxy.newProxyInstance(classLoader, new Class[]{classObject}, proxyHandler);
        mapCache.put(classObject, proxyInstance);
        return (T) proxyInstance;
    }
}
