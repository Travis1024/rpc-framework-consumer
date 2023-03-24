package com.space.travis.coder;

import com.space.travis.enumList.MessageTypeEnum;
import com.space.travis.enumList.SerializationTypeEnum;
import com.space.travis.pojo.MessageHeader;
import com.space.travis.pojo.MessageProtocol;
import com.space.travis.pojo.RpcRequestBody;
import com.space.travis.serialization.HessianSerialization;
import com.space.travis.serialization.JsonSerialization;
import com.space.travis.serialization.RpcSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName RpcEncoder
 * @Description Rpc消息编码器
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/23
 */
@Slf4j
public class RpcEncoder<T> extends MessageToByteEncoder<MessageProtocol<T>> {

    /*
    +---------------------------------------------------------------+
    | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
    +---------------------------------------------------------------+
    | 状态 1byte |        消息 ID 32byte     |      数据长度 4byte     |
    +---------------------------------------------------------------+
    ｜           消息体----RpcRequestBody、RpcResponseBody            ｜
    ｜ 请求消息体 (请求的服务名+版本号、请求调用的方法、参数列表、参数类型列表) ｜
    ｜ 响应消息体 (Object data、String message)                       ｜
    +---------------------------------------------------------------+
    */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageProtocol<T> tMessageProtocol, ByteBuf byteBuf) throws Exception {
        log.info("========进行消息编码========");
        MessageHeader messageHeader = tMessageProtocol.getMessageHeader();
        // 写魔数
        byteBuf.writeShort(messageHeader.getMagic());
        // 写协议版本号
        byteBuf.writeByte(messageHeader.getVersion());
        // 写序列化算法
        Byte serialization = messageHeader.getSerialization();
        byteBuf.writeByte(serialization);
        // 写报文类型
        Byte messageType = messageHeader.getMessageType();
        byteBuf.writeByte(messageType);
        // 写状态
        Byte status = messageHeader.getStatus();
        byteBuf.writeByte(status);
        // 写消息ID
        byteBuf.writeCharSequence(messageHeader.getRequestID(), StandardCharsets.UTF_8);

        // rpc编码器不需要关心消息的类型（request、response），直接进行消息体的序列化就可以
        MessageTypeEnum messageTypeByCode = MessageTypeEnum.getMessageTypeByCode(messageType);
        SerializationTypeEnum serializationType = SerializationTypeEnum.getSerializationTypeByCode(serialization);

        RpcSerialization rpcSerialization = null;
        switch (serializationType) {
            case JSON:
                rpcSerialization = new JsonSerialization();
                break;
            case HESSIAN:
                rpcSerialization = new HessianSerialization();
                break;
            default:
                throw new IllegalArgumentException("错误：消息序列化类型错误！");
        }
        byte[] messageBodyByte = rpcSerialization.serialize(tMessageProtocol.getMessageBody());
        int messageLength = messageBodyByte.length;
        byteBuf.writeInt(messageLength);
        byteBuf.writeBytes(messageBodyByte, 0 ,messageLength);
        log.info("消息编码成功！");

    }
}
