package com.example.demo.service.impl;

import com.example.demo.client.Demo2Client;
import com.example.demo.dao.TestMapper;
import com.example.demo.entity.Test;
import com.example.demo.service.DemoService;
import com.codingapi.tx.annotation.TxTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lorne on 2017/6/26.
 */
@Service
public class DemoServiceImpl implements DemoService {


    @Autowired
    private Demo2Client demo2Client;


    @Autowired
    private TestMapper testMapper;

    @Override
    public List<Test> list() {
        return testMapper.findAll();
    }

    @Override
    @TxTransaction(isStart = true)
    @Transactional
    public int save() {


        int rs2 = demo2Client.save();

        int rs1 = testMapper.save("shardingjdbc-hello-1");

        int v = 100/0;

        return rs1+rs2;
    }
}
