package com.space.travis.enumList;

import io.netty.util.internal.StringUtil;
import lombok.Getter;

/**
 * @ClassName SerializationTypeEnum
 * @Description 序列化类型枚举列表
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/23
 */
public enum SerializationTypeEnum {
    HESSIAN((byte) 0),
    JSON((byte) 1);

    @Getter
    private byte code;

    SerializationTypeEnum(byte code) {
        this.code = code;
    }

    /**
     * @MethodName getSerializationTypeByCode
     * @Description 根据序列化码获取序列化类型，异常则默认为HESSIAN
     * @Author travis-wei
     * @Data 2023/2/23
     * @param code
     * @Return com.space.travis.enumList.SerializationTypeEnum
     **/
    public static SerializationTypeEnum getSerializationTypeByCode(byte code) {
        for (SerializationTypeEnum typeEnum : SerializationTypeEnum.values()) {
            if (typeEnum.getCode() == code) {
                return typeEnum;
            }
        }
        // 如果序列化码异常，默认采用HESSIAN序列化方法
        return HESSIAN;
    }

    /**
     * @MethodName getCodeBySerialization
     * @Description 根据序列化类型获取序列化码，异常则默认为HESSIAN
     * @Author travis-wei
     * @Data 2023/2/23
     * @param typeEnum
     * @Return byte
     **/
    public static byte getCodeBySerialization(SerializationTypeEnum typeEnum) {
        // 如果传入的序列化类型异常，默认采用HESSIAN序列化方法
        return typeEnum != null ? typeEnum.getCode() : HESSIAN.getCode();
    }

    /**
     * @MethodName getCodeByString
     * @Description 根据序列化类型字符串获取序列化码，异常则默认为HESSIAN
     * @Author travis-wei
     * @Data 2023/2/23
     * @param serialization
     * @Return byte
     **/
    public static byte getCodeByString(String serialization) {
        // 如果传入的序列化类型字符串为空，默认采用HESSIAN序列化方法
        if (StringUtil.isNullOrEmpty(serialization)) return HESSIAN.getCode();
        for (SerializationTypeEnum typeEnum : SerializationTypeEnum.values()) {
            if (typeEnum.toString().equals(serialization)) {
                return typeEnum.getCode();
            }
        }
        return HESSIAN.getCode();
    }

}
