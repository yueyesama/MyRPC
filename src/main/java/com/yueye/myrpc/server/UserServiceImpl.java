package com.yueye.myrpc.server;

import com.yueye.myrpc.common.User;
import com.yueye.myrpc.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
@Slf4j
public class UserServiceImpl implements UserService {
    public User getUserById(Integer id) {
        log.debug("客户端查询了id为" + id + "的用户");
        // 模拟从数据库中取用户的行为
        User user = User.builder()
                .username(UUID.randomUUID().toString())
                .id(id)
                .sex("男")
                .build();
        return user;
    }

    @Override
    public Integer insertUserId(User user) {
        log.debug("插入数据成功：" + user);
        return 1;
    }
}
