package com.yueye.myrpc.server;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务暴露类
 * 接口名 与 服务端实现类 对应
 */
public class ServiceProvider {
    private Map<String, Object> interfaceProvider;

    public ServiceProvider() {
        this.interfaceProvider = new HashMap<>();
    }

    public void provideServiceInterface(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();

        for (Class<?> clazz : interfaces) {
            interfaceProvider.put(clazz.getName(), service);
        }
    }

    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }
}
