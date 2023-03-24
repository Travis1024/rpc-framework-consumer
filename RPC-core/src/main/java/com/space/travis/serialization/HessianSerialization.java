package com.space.travis.serialization;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import com.space.travis.exception.NullSerializableBodyException;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * @ClassName HessianSerialization
 * @Description Hessian序列化实现
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/2/26
 */
public class HessianSerialization implements RpcSerialization{
    @Override
    public <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new NullSerializableBodyException("错误:Hessian-serialize未找到可序列化的消息体!");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            HessianSerializerOutput hessianSerializerOutput = new HessianSerializerOutput(outputStream);
            hessianSerializerOutput.writeObject(obj);
            hessianSerializerOutput.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new SerializationException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        if (data == null) {
            throw new NullSerializableBodyException("错误:Hessian-deserialize未找到可序列化的消息体!");
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            HessianSerializerInput hessianSerializerInput = new HessianSerializerInput(inputStream);
            return (T) hessianSerializerInput.readObject(clazz);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SerializationException(e);
        }
    }
}
