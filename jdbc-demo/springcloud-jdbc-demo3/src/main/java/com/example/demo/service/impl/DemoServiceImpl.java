package com.example.demo.service.impl;

import com.codingapi.tx.annotation.TxTransaction;
import com.example.demo.client.Demo4Client;
import com.example.demo.client.Demo5Client;
import com.example.demo.dao.TestDao;
import com.example.demo.entity.Test;
import com.example.demo.service.DemoService;
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
    private Demo4Client demo4Client;

    @Autowired
    private Demo5Client demo5Client;

    @Override
    public List<Test> list() {
        return testDao.list();
    }

    @Override
    @Transactional
    @TxTransaction
    public int save() {

        int rs4 = demo4Client.save();

        int rs5 = demo5Client.save();

        int rs = testDao.save();

        //int v = 100/0;

        return rs + rs4 + rs5;
    }
}
