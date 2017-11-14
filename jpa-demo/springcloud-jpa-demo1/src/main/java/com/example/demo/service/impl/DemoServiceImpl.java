package com.example.demo.service.impl;

import com.codingapi.tx.annotation.TxTransaction;
import com.codingapi.tx.aop.bean.TxTransactionLocal;
import com.example.demo.client.Demo3Client;
import com.example.demo.dao.TestRepository;
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
    private Demo3Client demo3Client;


    @Autowired
    private TestRepository testRepository;

    @Override
    public List<Test> list() {
        return testRepository.findAll();
    }

    @Override
    @TxTransaction
    @Transactional
    public int save() {

        int rs2 = demo3Client.save();

        Test test = new Test();

        test.setName("jpa-hello-1-"+TxTransactionLocal.current().getGroupId());
        int rs1 = testRepository.save(test).getId();

       // int v = 100/0;

        return rs1+rs2;
    }
}
