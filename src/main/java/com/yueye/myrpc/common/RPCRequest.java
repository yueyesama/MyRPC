package com.yueye.myrpc.common;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 在一个 RPC 请求中，client 发送的应该是需要调用的 Service 接口名、方法名、参数、参数类型
 * 这样服务端可以通过反射调用相应的方法
 * 使用 Java 自带的序列化方式
 */
@Data
@Builder
public class RPCRequest implements Serializable {
    // 接口名，在服务端用接口指向实现类
    private String interfaceName;
    // 方法名
    private String methodName;
    // 参数列表
    private Object[] params;
    // 参数类型
    private Class<?>[] paramsTypes;
}
