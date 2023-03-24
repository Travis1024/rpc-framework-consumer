package com.space.travis.client.transport;

import com.space.travis.client.cache.LocalRpcProcessCache;
import com.space.travis.pojo.MessageProtocol;
import com.space.travis.pojo.RpcResponseBody;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName NettyRpcClientHandler
 * @Description 消息响应处理
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/3
 */
@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<MessageProtocol<RpcResponseBody>> {

    /**
     * 接收到服务端响应信息时执行的方法
     * @param channelHandlerContext
     * @param rpcResponseBodyMessageProtocol
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol<RpcResponseBody> rpcResponseBodyMessageProtocol) throws Exception {
        log.info("========已收到响应，且响应消息解码成功 -> 正在匹配请求与响应信息========");
        /**
         * 根据消息请求ID获取缓存的future对象
         */
        RpcFuture<MessageProtocol<RpcResponseBody>> rpcFuture = LocalRpcProcessCache.getFutureByRequestID(rpcResponseBodyMessageProtocol.getMessageHeader().getRequestID());
        if (rpcFuture == null) {
            log.error("请求与响应匹配失败！");
            throw new Exception("错误：找不到对应消息请求ID的缓存，消息ID可能出现损坏！");
        }
        log.info("匹配成功！");
        // 删除此请求对应的映射缓存
        LocalRpcProcessCache.deleteCache(rpcResponseBodyMessageProtocol.getMessageHeader().getRequestID());
        /**
         * 向异步执行结果类中注入结果，并解除rpcFuture.get()的阻塞状态
         */
        rpcFuture.setResponse(rpcResponseBodyMessageProtocol);
    }
}
