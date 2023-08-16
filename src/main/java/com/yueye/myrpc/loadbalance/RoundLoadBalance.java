package com.yueye.myrpc.loadbalance;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RoundLoadBalance implements LoadBalance{
    private int choose = -1;
    @Override
    public String balance(List<String> addressList) {
        choose++;
        choose = choose % addressList.size();
        log.debug("轮询负载均衡选择了" + choose + "服务器");
        return addressList.get(choose);
    }
}
