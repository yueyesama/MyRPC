package com.yueye.myrpc.server;

import com.yueye.myrpc.codec.JsonSerializer;
import com.yueye.myrpc.codec.MyDecode;
import com.yueye.myrpc.codec.MyEncode;
import com.yueye.myrpc.codec.ObjectSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.AllArgsConstructor;

/**
 * 初始化，主要负责序列化的编码解码，需要解决 netty 的粘包问题
 */
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceProvider;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 消息格式 [长度][消息体]，解决粘包问题
        pipeline.addLast(new LengthFieldBasedFrameDecoder(
                Integer.MAX_VALUE, 0, 4, 0, 4));

        // 计算当前待发送消息的长度，写入到前 4 个字节中
        pipeline.addLast(new LengthFieldPrepender(4));

        // 这里使用的还是 Java 序列化方式，netty 自带的解码编码支持传输这种结构
        /*pipeline.addLast(new ObjectEncoder());
        pipeline.addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(String className) throws ClassNotFoundException {
                return Class.forName(className);
            }
        }));*/

        // 使用自定义的编解码器
        pipeline.addLast(new MyDecode());
        pipeline.addLast(new MyEncode(new JsonSerializer()));

        pipeline.addLast(new NettyRPCServerHandler(serviceProvider));
    }
}
