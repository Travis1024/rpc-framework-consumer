package com.space.travis.coder;

import com.space.travis.enumList.MessageTypeEnum;
import com.space.travis.enumList.SerializationTypeEnum;
import com.space.travis.pojo.*;
import com.space.travis.serialization.HessianSerialization;
import com.space.travis.serialization.JsonSerialization;
import com.space.travis.serialization.RpcSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @ClassName RpcDecoder
 * @Description Rpc消息解码器
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/23
 */
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    /*
    +---------------------------------------------------------------+
    | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 消息类型 1byte  |
    +---------------------------------------------------------------+
    | 状态 1byte |        消息 ID 32byte     |      数据长度 4byte     |
    +---------------------------------------------------------------+
    ｜           消息体----RpcRequestBody、RpcResponseBody            ｜
    ｜ 请求消息体 (请求的服务名+版本号、请求调用的方法、参数列表、参数类型列表) ｜
    ｜ 响应消息体 (Object data、String message)                       ｜
    +---------------------------------------------------------------+
    */

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> outList) throws Exception {
        log.info("========已收到响应 -> 正在进行响应消息解码========");
        log.info(String.valueOf(byteBuf));
        // 当可读的字节数据 < 消息协议消息头的长度时，对该消息进行丢弃
        if (byteBuf.readableBytes() < ProtocolConstants.HEADER_TOTAL_LENGTH) {
            log.warn("可读字节数据小于消息协议规定长度，消息丢弃！");
            return;
        }
        byteBuf.markReaderIndex();

        // 获取协议的魔数：用来标识是否是dubbo协议的数据报
        short magic = byteBuf.readShort();
        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("错误：消息解码时出现错误，魔数非法！");
        }
        // 获取协议版本号
        byte version = byteBuf.readByte();
        // 获取序列化算法
        byte serialization = byteBuf.readByte();
        // 获取消息类型
        byte messageType = byteBuf.readByte();
        // 获取状态
        byte status = byteBuf.readByte();
        // 获取消息ID
        CharSequence requestID = byteBuf.readCharSequence(ProtocolConstants.REQUEST_ID_LENGTH, StandardCharsets.UTF_8);

        // 获取数据长度
        int messageLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < messageLength) {
            // 如果后续可读的字节数据长度 < 消息头中记录的数据长度，直接舍弃数据，并将byteBuf的为位置恢复到备份位置
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] data = new byte[messageLength];
        byteBuf.readBytes(data);

        // 根据消息类型码 获取消息类型(Request、Response)
        MessageTypeEnum messageTypeByCode = MessageTypeEnum.getMessageTypeByCode(messageType);
        if (messageTypeByCode == null) {
            return;
        }

        // 创建请求头，将消息中解码到的请求头信息赋值到请求头对象中
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setMagic(magic);
        messageHeader.setVersion(version);
        messageHeader.setSerialization(serialization);
        messageHeader.setMessageType(messageType);
        messageHeader.setStatus(status);
        messageHeader.setRequestID(String.valueOf(requestID));
        messageHeader.setMessageLength(messageLength);

        // 获取序列化类型
        SerializationTypeEnum serializationType = SerializationTypeEnum.getSerializationTypeByCode(serialization);
        RpcSerialization rpcSerialization = null;
        switch (serializationType) {
            case HESSIAN:
                rpcSerialization = new HessianSerialization();
                break;
            case JSON:
                rpcSerialization = new JsonSerialization();
            default:
                throw new IllegalArgumentException("错误：消息体序列化类型非法！");
        }

        switch (messageTypeByCode) {
            case REQUEST:
                RpcRequestBody requestBody = rpcSerialization.deserialize(data, RpcRequestBody.class);
                if (requestBody != null) {
                    MessageProtocol<RpcRequestBody> messageProtocol = new MessageProtocol<>();
                    messageProtocol.setMessageHeader(messageHeader);
                    messageProtocol.setMessageBody(requestBody);
                    outList.add(messageProtocol);
                }
                break;
            case RESPONSE:
                RpcResponseBody responseBody = rpcSerialization.deserialize(data, RpcResponseBody.class);
                if (responseBody != null) {
                    MessageProtocol<RpcResponseBody> messageProtocol = new MessageProtocol<>();
                    messageProtocol.setMessageHeader(messageHeader);
                    messageProtocol.setMessageBody(responseBody);
                    log.info("响应消息解码成功！");
                    outList.add(messageProtocol);
                }
                break;
            default:
                throw new IllegalArgumentException("错误：消息类型非法！非Request、Response类型！");
        }
    }
}
