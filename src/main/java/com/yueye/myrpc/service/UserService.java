package com.yueye.myrpc.service;

import com.yueye.myrpc.common.User;

public interface UserService {
    User getUserById(Integer id);

    Integer insertUserId(User user);
}
