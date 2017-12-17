package com.example.demo.dao;

import com.example.demo.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by lorne on 2017/6/28.
 */
public interface TestRepository extends JpaRepository<Test,Integer>{

}
