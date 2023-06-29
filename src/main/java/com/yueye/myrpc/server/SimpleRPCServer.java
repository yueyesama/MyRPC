package com.yueye.myrpc.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 该实现类使用 BIO 监听模式，来一个任务，就 new 一个线程去处理
 */
public class SimpleRPCServer implements RPCServer{
    // 服务接口名 : 服务接口对象
    private ServiceProvider serviceProvider;

    public SimpleRPCServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("BIO 版服务器已启动！");
            // BIO 的方式监听 Socket
            while (true) {
                Socket socket = serverSocket.accept();
                // 开启一个新线程去处理
                new Thread(new WorkThread(socket, serviceProvider)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("服务器启动失败！");
        }
    }

    @Override
    public void stop() {

    }
}
