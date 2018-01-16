package com.example.demo.service;

import com.codingapi.tx.annotation.ITransactionRunning;
import com.example.demo.entity.Test;

import java.util.List;

/**
 * Created by lorne on 2017/6/26.
 */
public interface DemoService extends ITransactionRunning {

    List<Test> list();

    int save();

}
