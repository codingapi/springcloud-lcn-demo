# springcloud LCN分布式事务示例demo


## 使用说明

框架分为jdbc／jpa／mybatis三个版本。各个版本之间除了DB框架差异以外，其他配置都相同。

mybatis版本的demo开启了hystrix  

关于hystrix的策略说明可参考：[http://www.jianshu.com/p/b8d21248c9b1](http://www.jianshu.com/p/b8d21248c9b1)

demo分为两类，demo1/demo2 只是消费者与提供者两个的简单demo。以及demo1/2/3/4/5复杂类型调用关系。

demo1/demo2类型：

demo1作为消费者（分布式事务的发起者）调用demo2.

demo1/2/3/4/5类型：

demo1作为分布式事务的发起者，调用了demo2 demo3，demo3有调用了demo4 demo5.

## 使用步骤

1. 见wiki[TxManager启动说明](https://github.com/1991wangliang/tx-lcn/wiki) 

2. 添加配置maven配置

```
    <dependency>
        <groupId>com.github.1991wangliang</groupId>
        <artifactId>springcloud-transaction</artifactId>
        <version>1.0.0</version>
    </dependency>
    
```

3. 添加tx.properties

```

#txmanager地址  http://txmanager ip:txmanager port/txmanager name/tx/manager/getServer 写法固定
url=http://127.0.0.1:8761/tx/manager/getServer

#事务补偿记录配置

#db 数据库类型 目前支持 mysql oracle sqlserver
compensate.db.dbType = mysql


```

4. 添加事务拦截器
```


@Aspect
@Component
public class TxTransactionInterceptor  implements Ordered{

    @Autowired
    private TxManagerInterceptor txManagerInterceptor;

    @Override
    public int getOrder() {
        return 1;
    }

    @Around("execution(* com.demo.service.impl.*Impl.*(..))")
    public Object around(ProceedingJoinPoint point)throws Throwable{
        return txManagerInterceptor.around(point);
    }
}

```

注意：  
@Around 拦截地址不能包含com.lorne.tx.*   
LCN是不控制事务。切面仅用于识别LCN分布式事务的作用。

5. 在消费者配置拦截器.


**`@FeignClient`的方式** 

若使用的是`@FeignClient`的方式，则需要添加`configuration`配置。


```
@FeignClient(value = "demo3",configuration = MyConfiguration.class)
public interface Demo3Client {


    @RequestMapping("/demo/list")
    List<Test> list();


    @RequestMapping("/demo/save")
    int save();
}

```

```

public class MyConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return new TransactionRestTemplateInterceptor();
    }
}

```

使用自定义Configuration添加`TransactionRestTemplateInterceptor`


**RestTemplate的方式**


在builder时添加拦截器

```

	@Autowired
	private RestTemplateBuilder builder;

	@Bean
	public RestTemplate restTemplate() {
		return builder.interceptors(new TransactionHttpRequestInterceptor()).build();
	}


```
在RestTemplateBuilder添加`TransactionHttpRequestInterceptor`拦截器


**传统的Http请求方式**

若采用的是传统的Http请求那么需要手动在发起请求的header下添加tx-group参数如下：

```

    TxTransactionLocal txTransactionLocal = TxTransactionLocal.current();
    String groupId = txTransactionLocal==null?null:txTransactionLocal.getGroupId();
    request.addHeader("tx-group",groupId);
        
```

支持传统的Http方式也就意味着支持http下的分布式事务

6. 配置项目的`application.properties`的配置文件。

```

spring.datasource.driver-class-name = com.mysql.jdbc.Driver
spring.datasource.url= jdbc:mysql://localhost:3306/test
spring.datasource.username= root
spring.datasource.password=root
spring.datasource.initialize =  true
init-db= true

spring.application.name = demo1
server.port = 8081
#${random.int[9000,9999]}
eureka.client.service-url.defaultZone=http://127.0.0.1:8761/eureka/

#Ribbon的负载均衡策略
ribbon.NFLoadBalancerRuleClassName=com.netflix.loadbalancer.RandomRule
#禁止Ribbon重试调用
ribbon.MaxAutoRetriesNextServer=0

```

7. 配置LCN代理和补偿连接池


```

    @Bean
	public DataSource dataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(env.getProperty("spring.datasource.url"));
		dataSource.setUsername(env.getProperty("spring.datasource.username"));//用户名
		dataSource.setPassword(env.getProperty("spring.datasource.password"));//密码
		dataSource.setInitialSize(2);
		dataSource.setMaxActive(20);
		dataSource.setMinIdle(0);
		dataSource.setMaxWait(60000);
		dataSource.setValidationQuery("SELECT 1");
		dataSource.setTestOnBorrow(false);
		dataSource.setTestWhileIdle(true);
		dataSource.setPoolPreparedStatements(false);

		LCNDataSourceProxy dataSourceProxy = new LCNDataSourceProxy();
		dataSourceProxy.setDataSource(dataSource);
		//分布式事务参与的最大连接数，确保不要超过普通连接池的最大值即可
		dataSourceProxy.setMaxCount(10);
		return dataSourceProxy;
	}
	
    @Bean
    public CompensateDataSource compensateDataSource() {

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));//用户名
        dataSource.setPassword(env.getProperty("spring.datasource.password"));//密码
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(5);
        dataSource.setMinIdle(0);
        dataSource.setMaxWait(60000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);
        dataSource.setPoolPreparedStatements(false);

        CompensateDataSource compensateDataSource = new CompensateDataSource();
        compensateDataSource.setDataSource(dataSource);
        return compensateDataSource;
    }
	
```

  有些用法发现用DataSource类型返回数据时有错误，建议修改为`LCNDataSourceProxy`
  
```

        @Bean
    	public LCNDataSourceProxy dataSource() {
    	    // ... ... 
    	    return ...;
    	}
```
    


8. 创建数据库，项目都是依赖相同的数据库，创建一次其他的demo下将不再需要重复创建。mysql数据库，库名称test

```

USE test;

DROP TABLE IF EXISTS `t_test`;

CREATE TABLE `t_test` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


```




## 注意事项

1. 禁止重名的bean对象。

事务的补偿机制是基于java反射的方式重新执行一次需要补偿的业务。因此执行的时候需要获取到业务的service对象，LCN是基于spring的ApplicationContent的getBean方法获取bean的对象的。因此不允许出现重名对象。


## 测试说明


demo1/demo2类型:

运行demo2下的XXXDemoApplication，再运行demo1下的XXXDemoApplication。

然后在浏览器访问http://127.0.0.1:8081/demo/save

效果：/by zero 异常所有事务都回滚。

说明： demo1都是消费者，默认在业务里添加了`int v = 100/0;`异常代码。因此在不注释的情况下事务回归。


demo1/2/3/4/5类型:
 
分别运行demo5、demo4、demo3、demo2下的XXXDemoApplication，再运行demo1下的XXXDemoApplication。

然后在浏览器访问http://127.0.0.1:8081/demo/save
 
效果：/by zero 异常所有事务都回滚。

说明：demo1和demo3是消费者，默认在业务里添加了`int v = 100/0;`，demo3这行已注释，默认回滚，全部注释掉会提交事务。


XXXDemoApplication 就是对应不同demo版本下的启动类。

技术交流群：554855843
