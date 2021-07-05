package com.dlut.toyRPC.client;

public interface HelloService {
    String hello(String name);

    String hello(Person person);
}
