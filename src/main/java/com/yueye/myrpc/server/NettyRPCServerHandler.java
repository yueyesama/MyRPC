package com.yueye.myrpc.server;

import com.yueye.myrpc.common.RPCRequest;
import com.yueye.myrpc.common.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 服务器端，接收到的请求格式是 RPCRequest
 * Object 类型也行，需要强制转型
 */
@AllArgsConstructor
@Slf4j
public class NettyRPCServerHandler extends SimpleChannelInboundHandler<RPCRequest> {

    private ServiceProvider serviceProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCRequest msg) throws Exception {
         RPCResponse response = getResponse(msg);
         ctx.writeAndFlush(response);
         ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务端 netty 出错！" + cause.getMessage());
        ctx.close();
    }

    public RPCResponse getResponse(RPCRequest request) {
        // 得到服务名
        String interfaceName = request.getInterfaceName();
        // 得到服务端相应的服务实现类
        Object service = serviceProvider.getService(interfaceName);
        try {
            // 反射调用服务方法，获取数据
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamsTypes());
            Object invoke = method.invoke(service, request.getParams());

            // 封装 response
            return RPCResponse.success(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("方法执行错误！" + e.getMessage());
            return RPCResponse.fall();
        }
    }
}
