package com.space.travis.client.transport;

import com.space.travis.client.cache.LocalRpcProcessCache;
import com.space.travis.coder.RpcDecoder;
import com.space.travis.coder.RpcEncoder;
import com.space.travis.pojo.MessageProtocol;
import com.space.travis.pojo.RequestMetaData;
import com.space.travis.pojo.RpcRequestBody;
import com.space.travis.pojo.RpcResponseBody;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName NettyRpcClient
 * @Description Netty初始化和发送请求
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/3/2
 */
@Slf4j
public class NettyRpcClient implements RpcRequestTransport{

    // 定义线程组
    private final EventLoopGroup eventLoopGroup;
    // 定义netty客户端启动器
    private final Bootstrap bootstrap;

    public NettyRpcClient() {
        // 创建线程组
        this.eventLoopGroup = new NioEventLoopGroup();
        // 创建netty客户端启动器
        this.bootstrap = new Bootstrap();
    }

    @Override
    public void initNetty() {
        /**
         * Bootstrap为netty客户端的启动器，ServerBootstrap是netty服务端的启动器
         * bootstrap作为客户端，主要用于发起对服务端的连接，发送信息到服务端和接收服务端的响应。
         */
        bootstrap
                // 设置线程组
                .group(eventLoopGroup)
                // 设置执行channel(客户端为NioSocketChannel，服务端为NioServerSocketChannel)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcDecoder())              // inbound 消息解码
                                .addLast(new NettyRpcClientHandler())   // inbound 对解码后的响应消息进行处理
                                .addLast(new RpcEncoder<>());           // outbound 消息编码
                    }
                });
    }

    @Override
    public MessageProtocol<RpcResponseBody> sendRequest(RequestMetaData requestMetaData) throws ExecutionException, InterruptedException, TimeoutException {
        MessageProtocol<RpcRequestBody> messageProtocol = requestMetaData.getMessageProtocol();
        // 创建异步执行结果类
        RpcFuture<MessageProtocol<RpcResponseBody>> rpcFuture = new RpcFuture<>();
        // 添加请求和响应的映射关系
        LocalRpcProcessCache.cacheAdd(messageProtocol.getMessageHeader().getRequestID(), rpcFuture);

        try {
            // bind and start to accept incoming connections
            ChannelFuture channelFuture = bootstrap.connect(requestMetaData.getAddress(), requestMetaData.getPort());
            channelFuture.sync();
            /**
             * 异步非阻塞式连接，调用channelFuture.addListener(),添加监听器，线程无需阻塞等待连接，可以继续向下执行代码逻辑 (这里只是监听连接是否成功，不是监听响应)
             * 当连接建立成功之后，其他线程会自动调用监听器中的operationComplete()方法, operationComplete()方法并非主线程调用，而是NioEventLoopGroup线程组中的一个NioEventLoop线程调用
             */
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture1) {
                    if (channelFuture1.isSuccess()) {
                        log.info("connect rpc server {} on port {} success.", requestMetaData.getAddress(), requestMetaData.getPort());
                    } else {
                        log.error("connect rpc server {} on port {} failed.", requestMetaData.getAddress(), requestMetaData.getPort());
                        log.error("执行出错：" + channelFuture1.cause().getMessage());
                    }
                }
            });
            // 建立连接成功之后，执行IO读写操作，写入数据
            // write和flush属于outbound事件，执行编码handler
            channelFuture.channel().writeAndFlush(messageProtocol);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            e.printStackTrace();
        }
        // 可以在主程序中顺序执行到这里，不被阻塞
        // 但是在rpcFuture.get()获取响应结果时会被阻塞，需要等待响应结果，当接收到响应时，首先执行的是解码和handler
        if (requestMetaData.getTimeOut() != null) {
            // 如果超时，则返回null
            return rpcFuture.get(requestMetaData.getTimeOut(), TimeUnit.SECONDS);
        }
        return rpcFuture.get();
    }
}
