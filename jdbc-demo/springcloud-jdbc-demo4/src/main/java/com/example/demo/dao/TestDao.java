package com.example.demo.dao;

import com.example.demo.entity.Test;

import java.util.List;

/**
 * Created by lorne on 2017/6/26.
 */
public interface TestDao {
    int save();

    List<Test> list();
}
