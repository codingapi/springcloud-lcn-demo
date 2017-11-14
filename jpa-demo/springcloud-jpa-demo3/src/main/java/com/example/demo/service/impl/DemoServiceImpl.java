package com.example.demo.service.impl;

import com.example.demo.client.Demo2Client;
import com.example.demo.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lorne on 2017/6/26.
 */
@Service
public class DemoServiceImpl implements DemoService {


    @Autowired
    private Demo2Client demo2Client;




    @Override
    public int save() {

        demo2Client.save();

        return 0;
    }
}
