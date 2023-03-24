package com.space.travis.enumList;

import lombok.Getter;

/**
 * @ClassName MessageTypeEnum
 * @Description 消息类型枚举列表
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/23
 */
public enum MessageTypeEnum {
    REQUEST((byte)1),
    RESPONSE((byte)2);

    @Getter
    private byte code;

    MessageTypeEnum(byte code) {
        this.code = code;
    }

    /**
     * @MethodName getMessageTypeByCode
     * @Description 根据消息类型码获取消息类型
     * @Author travis-wei
     * @Data 2023/2/23
     * @param code
     * @Return com.space.travis.enumList.MessageTypeEnum
     **/
    public static MessageTypeEnum getMessageTypeByCode(byte code) {
        for (MessageTypeEnum type : MessageTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
