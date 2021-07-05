package com.dlut.toyRPC.server;

import com.dlut.toyRPC.client.HelloService;
import com.dlut.toyRPC.client.Person;

@RpcService(HelloService.class)
public class HelloServiceImpl {
    public HelloServiceImpl(){

    }

    public String hello(String name){
        return "Hello! " + name;
    }

    public String hello(Person person){
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
