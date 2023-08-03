package com.yueye.myrpc.codec;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class ObjectSerializer implements Serializer {

    // 对象 -> 字节数组
    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes = null;
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();

        } catch (IOException e) {
            log.error("Java 原生序列化出错：" + e.getMessage());
        }
        return bytes;
    }

    // 字节数组 -> 对象
    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        try (
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bis)
        ) {
            obj = ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Java 原生反序列化出错：" + e.getMessage());
        }
        return obj;
    }

    @Override
    public int getType() {
        return 0;
    }
}
