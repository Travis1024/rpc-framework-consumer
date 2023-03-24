package com.space.travis.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

/**
 * @ClassName JsonSerialization
 * @Description JSON序列化实现
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/26
 */
public class JsonSerialization implements RpcSerialization{

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 允许出现未定义处理方法:（没有对应的setter方法或其他的处理器）的未知字段-false
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        // 设置日期格式
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public <T> byte[] serialize(T obj) {
        try {
            return obj instanceof String ? ((String) obj).getBytes() : OBJECT_MAPPER.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new SerializationException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(new String(data), clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new SerializationException(e);
        }
    }
}
