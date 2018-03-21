package com.example.demo.service.impl;

import com.example.demo.client.Demo2Client;
import com.example.demo.client.Demo3Client;
import com.example.demo.dao.TestDao;
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
    private TestDao testDao;

    @Autowired
    private Demo2Client demo2Client;

    @Autowired
    private Demo3Client demo3Client;


    @Override
    public List<Test> list() {
        return testDao.list();
    }

    @Override
    @TxTransaction(isStart = true)
    @Transactional
    public int save() {

        int rs2 = demo2Client.save();

        int rs3 = demo3Client.save();

        int rs1 = testDao.save();

        int v = 100/0;

        return rs1+rs2+rs3;
    }
}
