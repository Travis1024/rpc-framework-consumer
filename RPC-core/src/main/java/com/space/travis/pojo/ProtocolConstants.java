package com.space.travis.pojo;

/**
 * @ClassName ProtocolConstants
 * @Description 存放消息头的固定参数
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/23
 */
public class ProtocolConstants {
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
     * 魔数
     */
    public static final short MAGIC = 0x00;
    /**
     * 版本号
     */
    public static final byte VERSION = 0x1;
    /**
     * 消息ID（请求ID）的固定长度
     */
    public static final int REQUEST_ID_LENGTH = 32;
    /**
     * 消息头的总长度（固定长度）
     */
    public static final int HEADER_TOTAL_LENGTH = 2 + 1 + 1 + 1 + 1 + 32 + 4;

}
