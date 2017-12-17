package com.example.demo.client;

import org.springframework.stereotype.Component;


@Component
public class Demo3ClientHystric implements Demo3Client {


    @Override
    public int save() {
        System.out.println("进入断路器-save。。。");
        throw new RuntimeException("save 保存失败.");
    }
}
