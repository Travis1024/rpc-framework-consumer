package com.space.travis.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MessageProtocol
 * @Description 定义消息协议
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/23
 */
@Data
public class MessageProtocol<T> implements Serializable {

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
    /**
     * 消息头（前两行）
     */
    private MessageHeader messageHeader;
    /**
     * 消息体
     */
    private T messageBody;
}
