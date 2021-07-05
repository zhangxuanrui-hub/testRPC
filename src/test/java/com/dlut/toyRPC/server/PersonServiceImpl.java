package com.dlut.toyRPC.server;

import com.dlut.toyRPC.client.Person;
import com.dlut.toyRPC.client.PersonService;

import java.util.ArrayList;
import java.util.List;

@RpcService(PersonService.class)
public class PersonServiceImpl {
    public List<Person>GetTestPerson(String name,int num){
        List<Person>persons = new ArrayList<>(num);
        for(int i = 0;i < num;i++){
            persons.add(new Person(Integer.toString(i),name));
        }
        return persons;
    }
}
