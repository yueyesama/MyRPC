package com.yueye.myrpc.client;

import com.yueye.myrpc.common.RPCRequest;
import com.yueye.myrpc.common.RPCResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class IOClient {
    // 这里负责底层与服务端的通信，发送 request，接收 response
    // 这里的 request 需要上层进行封装，根据不同的 service 进行不同的封装
    public static RPCResponse sendRequest(String host, int port, RPCRequest request) {
        try {
            Socket socket = new Socket(host, port);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            System.out.println(request);
            oos.writeObject(request);
            oos.flush();

            RPCResponse response = (RPCResponse) ois.readObject();

            return response;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("发送出现错误！");
            return null;
        }
    }
}
