package com.yueye.myrpc.client;

import com.yueye.myrpc.common.Blog;
import com.yueye.myrpc.common.User;
import com.yueye.myrpc.service.BlogService;
import com.yueye.myrpc.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestClient {

    public static void main(String[] args) {
        // 创建一个使用 netty 传输的客户端
        RPCClient rpcClient = new NettyRPCClient();
        // 创建代理对象
        RPCClientProxy rpcClientProxy = new RPCClientProxy(rpcClient);

        // 调用代理对象方法
        UserService userService = rpcClientProxy.getProxy(UserService.class);
        User userById = userService.getUserById(15);
        log.info("从服务端得到的 user 为：" + userById);

        User user = User.builder().id(74751).username("超先生").sex("嬲").build();
        Integer integer = userService.insertUserId(user);
        log.info("向服务端插入数据：" + integer);

        BlogService blogService = rpcClientProxy.getProxy(BlogService.class);
        Blog blogById = blogService.getBlogById(12);
        log.info("从服务端得到的 blog 为：" + blogById);

    }
}
