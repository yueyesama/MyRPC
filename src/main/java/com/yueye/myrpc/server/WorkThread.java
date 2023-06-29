package com.yueye.myrpc.server;

import com.yueye.myrpc.common.RPCRequest;
import com.yueye.myrpc.common.RPCResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

@AllArgsConstructor
@Slf4j
public class WorkThread implements Runnable{
    private Socket socket;
    private ServiceProvider serviceProvider;

    @Override
    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

            // 读取客户端传过来的 request
            RPCRequest request = (RPCRequest) ois.readObject();
            // 反射调用服务方法获得返回值
            RPCResponse response = getResponse(request);

            // 写入客户端
            oos.writeObject(response);
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            log.error("从IO中读取数据出错！" + e.getMessage());
        }
    }

    private RPCResponse getResponse(RPCRequest request) {
        // 得到服务端相应的服务实现类
        String interfaceName = request.getInterfaceName();
        Object service = serviceProvider.getService(interfaceName);

        try {
            // 反射调用对应的方法
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamsTypes());
            Object invoke = method.invoke(service, request.getParams());

            // 封装，写入 response 对象
            return RPCResponse.success(invoke);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("服务执行错误！" + e.getMessage());
            return RPCResponse.fall();
        }
    }
}
