package com.space.travis.client.cache;

import com.space.travis.pojo.MessageProtocol;
import com.space.travis.pojo.RpcResponseBody;
import com.space.travis.client.transport.RpcFuture;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName LocalRpcProcessCache
 * @Description 请求和响应映射对象
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/3
 */
public class LocalRpcProcessCache {

    // 请求响应映射对象缓存
    private static Map<String, RpcFuture<MessageProtocol<RpcResponseBody>>> requestResponseCache = new HashMap<>();

    /**
     * @MethodName cacheAdd
     * @Description 添加请求和响应的映射关系
     * @Author travis-wei
     * @Data 2023/3/3
     * @param requestID
     * @param rpcFuture
     * @Return void
     **/
    public static void cacheAdd(String requestID, RpcFuture<MessageProtocol<RpcResponseBody>> rpcFuture) {
        requestResponseCache.put(requestID, rpcFuture);
    }

    /**
     * @MethodName getFutureByRequestID
     * @Description 根据消息请求ID获取缓存的future对象
     * @Author travis-wei
     * @Data 2023/3/3
     * @param requestID
     * @Return java.util.concurrent.Future<com.space.travis.pojo.MessageProtocol<com.space.travis.pojo.RpcResponseBody>>
     **/
    public static RpcFuture<MessageProtocol<RpcResponseBody>> getFutureByRequestID(String requestID) {
        if (requestResponseCache.containsKey(requestID)) {
            return requestResponseCache.get(requestID);
        }
        return null;
    }

    /**
     * @MethodName deleteCache
     * @Description 根据请求ID删除缓存
     * @Author travis-wei
     * @Data 2023/3/3
     * @param requestID
     * @Return void
     **/
    public static void deleteCache(String requestID) {
        requestResponseCache.remove(requestID);
    }


}
