package com.space.travis.enumList;

import lombok.Getter;

/**
 * @ClassName MessageStatusEnum
 * @Description 定义消息状态枚举列表
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/23
 */
public enum MessageStatusEnum {
    SUCCESS((byte)0),
    FAIL((byte)1),
    WAIT((byte)2);

    @Getter
    private byte code;

    MessageStatusEnum(byte code) {
        this.code = code;
    }

    /**
     * @MethodName isSuccess
     * @Description 根据状态码判断状态是否为成功状态
     * @Author travis-wei
     * @Data 2023/2/23
     * @param code
     * @Return boolean
     **/
    public static boolean judgeSuccessByCode(byte code) {
        return SUCCESS.code == code;
    }

}
