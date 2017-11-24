package com.example.demo.client;

import com.example.demo.entity.Test;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class Demo2ClientHystric implements Demo2Client {


    @Override
    public List<Test> list() {
        System.out.println("进入断路器-list。。。");
        return null;
    }

    @Override
    public int save() {

        System.out.println("进入断路器-save。。。");

       // int v = 1/0;
        throw new RuntimeException("调用失败.");
       // return 0;
    }
}
