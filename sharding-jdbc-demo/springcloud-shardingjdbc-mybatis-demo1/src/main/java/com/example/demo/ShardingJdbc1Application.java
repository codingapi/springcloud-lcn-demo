package com.example.demo;

import com.alibaba.druid.pool.DruidDataSource;
import io.shardingjdbc.core.api.ShardingDataSourceFactory;
import io.shardingjdbc.core.api.config.ShardingRuleConfiguration;
import io.shardingjdbc.core.api.config.TableRuleConfiguration;
import io.shardingjdbc.core.api.config.strategy.InlineShardingStrategyConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@EnableAutoConfiguration
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class ShardingJdbc1Application {

	public static void main(String[] args) {
		SpringApplication.run(ShardingJdbc1Application.class, args);
	}






	@Bean
	public DataSource shardingDataSource() {

		Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();

		DruidDataSource dataSource1 = new DruidDataSource();
		dataSource1.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource1.setUrl("jdbc:mysql://localhost:3306/db1");
		dataSource1.setUsername("root");
		dataSource1.setPassword("root");
		dataSourceMap.put("db1", dataSource1);

		DruidDataSource dataSource2 = new DruidDataSource();
		dataSource2.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource2.setUrl("jdbc:mysql://localhost:3306/db2");
		dataSource2.setUsername("root");
		dataSource2.setPassword("root");
		dataSourceMap.put("db2", dataSource2);

		TableRuleConfiguration orderTableRuleConfig = new TableRuleConfiguration();
		orderTableRuleConfig.setLogicTable("t_test");
		orderTableRuleConfig.setActualDataNodes("db${1..2}.t_test${2..1}");

		orderTableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("user_id", "ds_${user_id % 2}"));
		orderTableRuleConfig.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("order_id", "t_order_${order_id % 2}"));

		ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
		shardingRuleConfig.getTableRuleConfigs().add(orderTableRuleConfig);

		// config order_item table rule...


		DataSource dataSource = null;
		try {
			dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dataSource;
	}

}
