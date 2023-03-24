package com.space.travis.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RpcResponseBody
 * @Description Rpc响应消息体
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/23
 */
@Data
public class RpcResponseBody implements Serializable {
    /**
     * 用来存放服务端反射调用方法时，方法返回的结果，最后返回到消费者
     */
    private Object data;

    /**
     * 如果服务端反射调用方法时出现错误，存放错误信息，最后返回到消费者
     */
    private String message;
}
