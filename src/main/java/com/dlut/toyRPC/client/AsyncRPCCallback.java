package com.dlut.toyRPC.client;

public interface AsyncRPCCallback {
    void success(Object result);

    void fail(Exception e);
}
