package com.yueye.myrpc.server;

import com.yueye.myrpc.common.Blog;
import com.yueye.myrpc.service.BlogService;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.BlogServiceImpl")
public class BlogServiceImpl implements BlogService {
    @Override
    public Blog getBlogById(Integer id) {
        Blog blog = Blog.builder().id(id).userId(22).title("xx博客").build();
        log.debug("客户端查询了id为" + id + "的博客");
        return blog;
    }
}