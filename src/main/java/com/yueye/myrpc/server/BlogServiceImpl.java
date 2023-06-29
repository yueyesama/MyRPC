package com.yueye.myrpc.server;

import com.yueye.myrpc.common.Blog;
import com.yueye.myrpc.service.BlogService;

public class BlogServiceImpl implements BlogService {
    @Override
    public Blog getBlogById(Integer id) {
        Blog blog = Blog.builder().id(id).userId(22).title("xx博客").build();
        System.out.println("客户端查询了" + id + "博客");
        return blog;
    }
}