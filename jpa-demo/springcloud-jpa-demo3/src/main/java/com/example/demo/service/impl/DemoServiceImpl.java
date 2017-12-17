package com.example.demo.service.impl;

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
@Transactional
public class DemoServiceImpl implements DemoService {

    @Autowired
    private TestRepository testRepository;

    @Override
    public List<Test> list() {
        return testRepository.findAll();
    }

    @Override
    public int save() {

        Test test = new Test();

        test.setName("jpa-hello-3");
        int rs = testRepository.save(test).getId();

        return rs;
    }
}
