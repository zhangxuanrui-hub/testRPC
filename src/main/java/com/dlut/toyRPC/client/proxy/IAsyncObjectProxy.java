package com.dlut.toyRPC.client.proxy;

import com.dlut.toyRPC.client.RPCFuture;

public interface IAsyncObjectProxy {
    public RPCFuture call(String funcName,Object... args);
}
