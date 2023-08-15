package com.yueye.myrpc.client;

import com.yueye.myrpc.common.RPCRequest;
import com.yueye.myrpc.common.RPCResponse;
import com.yueye.myrpc.register.ServiceRegister;
import com.yueye.myrpc.register.ZkServiceRegister;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@AllArgsConstructor
@Slf4j
// 使用 Java BIO
public class SimpleRPCClient implements RPCClient{
    private String host;
    private int port;
    private ServiceRegister serviceRegister;
    public SimpleRPCClient() {
        // 初始化注册中心，建立连接
        this.serviceRegister = new ZkServiceRegister();
    }

    // 客户端发起一次请求调用，Socket 建立连接，发送 request，接收 response
    // request 需要在上层根据不同的 Service 进行封装（动态代理）
    @Override
    public RPCResponse sendRequest(RPCRequest request) {
        // 从注册中心获取host，port
        InetSocketAddress address = serviceRegister.serviceDiscovery(request.getInterfaceName());
        host = address.getHostName();
        port = address.getPort();

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
