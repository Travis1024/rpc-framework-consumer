package com.space.travis.pojo;

import com.space.travis.enumList.MessageStatusEnum;
import com.space.travis.enumList.MessageTypeEnum;
import com.space.travis.enumList.SerializationTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * @ClassName MessageHeader
 * @Description 消息头定义
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/23
 */
@Data
public class MessageHeader implements Serializable {

    /*
    +---------------------------------------------------------------+
    | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
    +---------------------------------------------------------------+
    | 状态 1byte |        消息 ID 32byte     |      数据长度 4byte     |
    +---------------------------------------------------------------+
    */

    /**
     * 魔数，用来标识是否是dubbo协议的数据包
     */
    private Short magic;
    /**
     * 协议版本号
     */
    private Byte version;
    /**
     * 序列化算法
     */
    private Byte serialization;
    /**
     * 报文类型
     */
    private Byte messageType;
    /**
     * 状态
     */
    private Byte status;
    /**
     * 消息ID，唯一标识消息
     */
    private String requestID;
    /**
     * 数据长度
     */
    private Integer messageLength;

    public static MessageHeader build(String serialization) {
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setMagic(ProtocolConstants.MAGIC);
        messageHeader.setVersion(ProtocolConstants.VERSION);
        messageHeader.setSerialization(SerializationTypeEnum.getCodeByString(serialization));
        // UUID生成的随机数一共包含36位，其中有4位为"-"，所以进行替换之后，一共包含32位。
        messageHeader.setRequestID(UUID.randomUUID().toString().replaceAll("-", ""));
        // 只有主动发送请求时才需要主动构建请求头，所以消息类型为请求类型（Request）
        messageHeader.setMessageType(MessageTypeEnum.REQUEST.getCode());
        // 消费者主动发送请求消息时，消息状态设置为WAIT
        messageHeader.setStatus(MessageStatusEnum.WAIT.getCode());
        // 数据长度在进行序列化后会进行重新计算并赋值，所以在此处不需要进行赋值
        // 消息状态只有消息接受者需要设定
        return messageHeader;

    }

}
