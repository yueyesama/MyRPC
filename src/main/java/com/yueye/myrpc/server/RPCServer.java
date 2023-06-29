package com.yueye.myrpc.server;

// 把 RPCServer 抽象成接口，以后的服务端实现这个接口即可
public interface RPCServer {
    void start(int port);
    void stop();
}
