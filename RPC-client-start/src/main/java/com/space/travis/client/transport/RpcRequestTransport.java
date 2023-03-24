package com.space.travis.client.transport;

import com.space.travis.pojo.MessageProtocol;
import com.space.travis.pojo.RequestMetaData;
import com.space.travis.pojo.RpcResponseBody;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName RpcRequestTransport
 * @Description 网络传输层，初始化netty服务端口，netty发送请求
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/2
 */
public interface RpcRequestTransport {
    /**
     * 初始化Netty服务
     */
    void initNetty();

    /**
     * netty发送请求
     * @param requestMetaData
     * @return
     */
    MessageProtocol<RpcResponseBody> sendRequest(RequestMetaData requestMetaData) throws ExecutionException, InterruptedException, TimeoutException;
}
