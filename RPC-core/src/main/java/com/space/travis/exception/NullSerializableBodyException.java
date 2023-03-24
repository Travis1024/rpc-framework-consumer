package com.space.travis.exception;

/**
 * @ClassName NullSerializableBodyException
 * @Description 未找到可序列化的消息体对象
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/26
 */
public class NullSerializableBodyException extends RuntimeException {
    public NullSerializableBodyException() {
        super();
    }

    public NullSerializableBodyException(String message) {
        super(message);
    }

    public NullSerializableBodyException(String message, Throwable cause) {
        super(message, cause);
    }
}
