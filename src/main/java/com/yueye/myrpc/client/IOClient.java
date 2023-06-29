package com.yueye.myrpc.client;

import com.yueye.myrpc.common.RPCRequest;
import com.yueye.myrpc.common.RPCResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
public class IOClient {
    // 这里负责底层与服务端的通信，发送 request，接收 response
    // 这里的 request 需要上层进行封装，根据不同的 service 进行不同的封装
    public static RPCResponse sendRequest(String host, int port, RPCRequest request) {
        try {
            Socket socket = new Socket(host, port);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            log.info("发送的 request 为：" + request);
            oos.writeObject(request);
            oos.flush();

            RPCResponse response = (RPCResponse) ois.readObject();
            log.info("接收的 response 为：" + response);

            return response;

        } catch (IOException | ClassNotFoundException e) {
            log.error("发送出现错误！" + e.getMessage());
            return null;
        }
    }
}
