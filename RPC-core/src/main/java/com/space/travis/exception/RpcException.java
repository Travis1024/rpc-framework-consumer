package com.space.travis.exception;

/**
 * @ClassName RpcException
 * @Description 定义异常信息类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/21
 */
public class RpcException extends RuntimeException{
    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
