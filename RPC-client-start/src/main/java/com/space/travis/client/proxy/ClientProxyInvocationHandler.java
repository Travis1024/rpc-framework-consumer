package com.space.travis.client.proxy;

import com.space.travis.client.cache.LocalRpcProcessCache;
import com.space.travis.client.config.RpcClientProperties;
import com.space.travis.client.discover.ClientDiscoverService;
import com.space.travis.enumList.MessageStatusEnum;
import com.space.travis.exception.NotFoundServiceInstanceException;
import com.space.travis.exception.RpcException;
import com.space.travis.pojo.*;
import com.space.travis.client.transport.NettyRpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @ClassName ClientProxyInvocationHandler
 * @Description 执行代理对象重新编写的方法
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/2
 */
@Slf4j
public class ClientProxyInvocationHandler implements InvocationHandler {


    private RpcClientProperties rpcClientProperties;
    private ClientDiscoverService clientDiscoverService;
    private NettyRpcClient nettyRpcClient;
    private Class<?> classObject;
    private String version;

    public ClientProxyInvocationHandler(Class<?> classObject, String version) {
        this.classObject = classObject;
        this.version = version;
    }

    public ClientProxyInvocationHandler(RpcClientProperties rpcClientProperties, ClientDiscoverService clientDiscoverService, NettyRpcClient nettyRpcClient, Class<?> classObject, String version) {
        this.rpcClientProperties = rpcClientProperties;
        this.clientDiscoverService = clientDiscoverService;
        this.nettyRpcClient = nettyRpcClient;
        this.classObject = classObject;
        this.version = version;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String serverName = "com.space.travis.api." + classObject.getSimpleName() + "-" + version;
        ServiceInfo serviceInfo = clientDiscoverService.discovery(serverName);
        if (serviceInfo == null) {
            throw new NotFoundServiceInstanceException("错误：未找到所需的服务实例！");
        }
        RequestMetaData requestMetaData = new RequestMetaData();
        requestMetaData.setAddress(serviceInfo.getAddress());
        requestMetaData.setPort(serviceInfo.getPort());
        requestMetaData.setTimeOut(rpcClientProperties.getTimeout());

        MessageProtocol<RpcRequestBody> messageProtocol = new MessageProtocol<>();
        // 初始化请求头
        MessageHeader messageHeader = MessageHeader.build(rpcClientProperties.getSerialization());
        // 初始化请求体
        RpcRequestBody rpcRequestBody = new RpcRequestBody();
        rpcRequestBody.setServiceName(serverName);
        rpcRequestBody.setMethod(method.getName());
        rpcRequestBody.setParameterTypes(method.getParameterTypes());
        rpcRequestBody.setParameters(args);
        // 设置请求头和请求体
        messageProtocol.setMessageHeader(messageHeader);
        messageProtocol.setMessageBody(rpcRequestBody);

        // 设置消息协议
        requestMetaData.setMessageProtocol(messageProtocol);
        // 通过netty发送请求
        MessageProtocol<RpcResponseBody> responseBodyMessageProtocol = nettyRpcClient.sendRequest(requestMetaData);

        if (responseBodyMessageProtocol == null) {
            // 删除此请求对应的映射缓存
            LocalRpcProcessCache.deleteCache(requestMetaData.getMessageProtocol().getMessageHeader().getRequestID());
            log.error("------请求超时! 当前设定的超时时间为 {} 秒------", rpcClientProperties.getTimeout());
            throw new RpcException("请求超时！");
        }
        if (responseBodyMessageProtocol.getMessageHeader().getStatus() == MessageStatusEnum.FAIL.getCode()) {
            // 删除此请求对应的映射缓存
            LocalRpcProcessCache.deleteCache(requestMetaData.getMessageProtocol().getMessageHeader().getRequestID());
            log.error("------rpc调用失败，message：{}------", responseBodyMessageProtocol.getMessageBody().getMessage());
            throw new RpcException(responseBodyMessageProtocol.getMessageBody().getMessage());
        }
        return responseBodyMessageProtocol.getMessageBody().getData();
    }
}
