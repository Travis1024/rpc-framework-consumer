package com.space.travis.exception;

/**
 * @ClassName NotFoundServiceInstanceException
 * @Description 定义未在zookeeper中找到服务实例的异常
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/21
 */
public class NotFoundServiceInstanceException extends RuntimeException{
    public NotFoundServiceInstanceException() {
    }

    public NotFoundServiceInstanceException(String message) {
        super(message);
    }

    public NotFoundServiceInstanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
