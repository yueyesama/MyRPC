package com.yueye.myrpc.codec;

public interface Serializer {
    // 对象序列化成字节数组
    byte[] serialize(Object obj);

    // 从字节数组反序列化成消息
    // 自定义序列化方式需指定消息格式，再根据message转化成相应的对象
    Object deserialize(byte[] bytes, int messageType);

    // 返回使用的序列器，是哪个
    // 0：java自带序列化方式, 1: json序列化方式
    int getType();

    // 根据序号取出序列化器
    // 暂时有两种实现方式，需要其它方式，实现这个接口即可
    static Serializer getSerializerByCode(int code) {
        switch (code) {
            case 0:
                return new ObjectSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
}
