package com.space.travis.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RpcRequestBody
 * @Description Rpc请求消息体
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/23
 */
@Data
public class RpcRequestBody implements Serializable {

    /**
     * 请求的服务名 + 版本号
     */
    private String serviceName;
    /**
     * 请求调用的方法
     */
    private String method;
    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;
    /**
     * 参数
     */
    private Object[] parameters;
}
