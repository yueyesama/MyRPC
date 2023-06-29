package com.yueye.myrpc.client;

import com.yueye.myrpc.common.Blog;
import com.yueye.myrpc.common.User;
import com.yueye.myrpc.service.BlogService;
import com.yueye.myrpc.service.UserService;


public class RPCClient {
    public static void main(String[] args) {
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 8899);
        UserService userService = clientProxy.getProxy(UserService.class);

        // 服务的方法1
        User userById = userService.getUserById(10);
        System.out.println("从服务端得到的 user 为：" + userById);

        // 服务的方法2
        User user = User.builder().id(100).username("张三").sex("嬲").build();
        Integer integer = userService.insertUserId(user);
        System.out.println("向服务端插入数据：" + integer);


        BlogService blogService = clientProxy.getProxy(BlogService.class);
        // 服务的方法3
        Blog blogById = blogService.getBlogById(10);
        System.out.println("从服务端得到的 blog 为：" + blogById);

    }
}
