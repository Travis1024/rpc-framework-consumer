package com.space.travis.serialization;

/**
 * @ClassName RpcSerialization
 * @Description 创建Rpc消息体序列化接口
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/24
 */
public interface RpcSerialization {
    /**
     * @MethodName serialize
     * @Description 对象序列化
     * @Author travis-wei
     * @Data 2023/2/24
     * @param obj 序列化对象
     * @param <T> 序列化对象原始类型
     * @Return byte[] 字节数组
     **/
    <T> byte[] serialize(T obj);

    /**
     * @MethodName deserialize
     * @Description 对象反序列化
     * @Author travis-wei
     * @Data 2023/2/24
     * @param data 序列化字节数组
     * @param clazz	原始类型的类对象
     * @param <T> 原始类型
     * @Return T
     **/
    /**
     * 第一个T表示<T>是一个泛型
     * 第二个T表示方法返回的是T类型的数据
     * 第三个T表示Class传入的数据是T类型
     */
    <T> T deserialize(byte[] data, Class<T> clazz);
}
