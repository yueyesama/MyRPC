package com.yueye.myrpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class NettyRPCServer implements RPCServer{

    private ServiceProvider serviceProvider;

    @Override
    public void start(int port) {
        // netty 事件循环组 boss 负责建立连接，work 负责具体的请求
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        log.debug("Netty 服务端已启动！");

        try {
            // 启动 netty 服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 初始化
            serverBootstrap.group(bossGroup, workGroup) // 1. 线程池 + Selector
                    .channel(NioServerSocketChannel.class) // 2. 选择基于 NIO 的服务端实现
                    .childHandler(new NettyServerInitializer(serviceProvider)); // 3.
            // 同步阻塞
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync(); // 指定 channel 监听的端口
            // 死循环监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动 netty 服务器失败！" + e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }
}
