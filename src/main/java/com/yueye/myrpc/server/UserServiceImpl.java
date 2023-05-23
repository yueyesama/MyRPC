package com.yueye.myrpc.server;

import com.yueye.myrpc.common.User;
import com.yueye.myrpc.service.UserService;

import java.util.UUID;

public class UserServiceImpl implements UserService {
    public User getUserById(Integer id) {
        System.out.println("客户端查询了id为" + id + "的用户");
        // 模拟从数据库中取用户的行为
        User user = User.builder()
                .username(UUID.randomUUID().toString())
                .id(id)
                .sex("男")
                .build();
        return user;
    }
}
