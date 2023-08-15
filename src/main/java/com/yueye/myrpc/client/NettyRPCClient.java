package com.yueye.myrpc.client;

import com.yueye.myrpc.common.RPCRequest;
import com.yueye.myrpc.common.RPCResponse;
import com.yueye.myrpc.register.ServiceRegister;
import com.yueye.myrpc.register.ZkServiceRegister;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyRPCClient implements RPCClient {
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;
    private String host;
    private int port;
    private ServiceRegister serviceRegister;

    public NettyRPCClient() {
        // 初始化注册中心，建立连接
        this.serviceRegister = new ZkServiceRegister();
    }

    // netty 客户端初始化
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup) // 同 Server
                .channel(NioSocketChannel.class) // 基于 NIO 的客户端实现
                .handler(new NettyClientInitializer());
    }


    // 这里需要操作一下，因为 netty 的传输都是异步的
    // 发送 request 后，会立刻返回，而不是想要的 response
    @Override
    public RPCResponse sendRequest(RPCRequest request) {

        // 从注册中心获取host，port
        InetSocketAddress address = serviceRegister.serviceDiscovery(request.getInterfaceName());
        host = address.getHostName();
        port = address.getPort();

        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port) // 指定要连接的服务器和端口
                    .sync(); // connect 是异步的，sync 方法等待建立连接完毕
            Channel channel = channelFuture.channel();

            log.info("发送的 request 为：" + request);
            // 发送数据
            channel.writeAndFlush(request);
            channel.closeFuture().sync();

            // 阻塞的获得结果，通过给 channel 设计别名，获取特定名字下的 channel 中的内容（在 handler 中设置）
            // AttributeKey 是线程隔离的，不会有线程安全问题
            // 实际上不应该通过阻塞，可通过回调函数
            AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse");
            RPCResponse response = channel.attr(key).get();

            log.info("接收的 response 为：" + response);
            return response;
        } catch (InterruptedException e) {
            log.error("客户端 netty 出错！" + e.getMessage());
        }
        return null;
    }
}
