package com.yueye.myrpc.common;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 传输对象抽象为 Object
 * 引入状态码和状态信息表示服务调用成功还是失败
 */
@Data
@Builder
public class RPCResponse implements Serializable {
    // 状态信息
    private int code;
    private String message;
    // 具体数据
    private Object data;

    public static RPCResponse success(Object data) {
        return RPCResponse.builder().code(200).data(data).build();
    }

    public static RPCResponse fall() {
        return RPCResponse.builder().code(500).message("服务器发生错误！").build();
    }

}
