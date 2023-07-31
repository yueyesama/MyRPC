package com.yueye.myrpc.client;

import com.yueye.myrpc.common.RPCRequest;
import com.yueye.myrpc.common.RPCResponse;

public interface RPCClient {
    RPCResponse sendRequest(RPCRequest request);
}
