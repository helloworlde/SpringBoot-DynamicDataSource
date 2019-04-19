# Spring Boot 和 MyBatis 实现多数据源、动态数据源切换

> 本项目使用 Spring Boot 和 MyBatis 实现多数据源，动态数据源的切换；有多种不同的实现方式，在学习的过程中发现没有文章将这些方式和常见的问题集中处理，所以将常用的方式和常见的问题都写在了在本项目的不同分支上：

- [master](https://github.com/helloworlde/SpringBoot-DynamicDataSource): 使用了多数据源的 RESTful API 接口，使用 Druid 实现了 DAO 层数据源动态切换和只读数据源负载均衡
- [dev](https://github.com/helloworlde/SpringBoot-DynamicDataSource/tree/dev): 最简单的切面和注解方式实现的动态数据源切换
- [druid](https://github.com/helloworlde/SpringBoot-DynamicDataSource/tree/druid): 通过切面和注解方式实现的使用 Druid 连接池的动态数据源切换
- [aspect_dao](https://github.com/helloworlde/SpringBoot-DynamicDataSource/tree/aspect_dao): 通过切面实现的 DAO 层的动态数据源切换
- [roundrobin](https://github.com/helloworlde/SpringBoot-DynamicDataSource/tree/roundrobin): 通过切面使用轮询方式实现的只读数据源负载均衡
- [hikari](https://github.com/helloworlde/SpringBoot-DynamicDataSource/tree/hikari): 升级到SpringBoot 2.0版本 数据源使用 Hikari

> 以上分支都是基于 dev 分支修改或扩充而来，基本涵盖了常用的多数据源动态切换的方式，基本的原理都一样，都是通过切面根据不同的条件在执行数据库操作前切换数据源

### 在使用的过程中基本踩遍了所有动态数据源切换的坑，将常见的一些坑和解决方法写在了 [Issues](https://github.com/helloworlde/SpringBoot-DynamicDataSource/blob/master/Issues.md) 里面


> 该项目使用了一个可写数据源和多个只读数据源，为了减少数据库压力，使用轮循的方式选择只读数据源；考虑到在一个 Service 中同时会有读和写的操作，所以本应用使用 AOP 切面通过 DAO 层的方法名切换只读数据源；但这种方式要求数据源主从一致，并且应当避免在同一个 Service 方法中写入后立即查询，如果必须在执行写入操作后立即读取，应当在 Service 方法上添加 `@Transactional` 注解以保证使用主数据源 

> 需要注意的是，使用 DAO 层切面后不应该在 Service 类层面上加 `@Transactional` 注解，而应该添加在方法上，这也是 Spring 推荐的做法

> 动态切换数据源依赖 `configuration` 包下的4个类来实现，分别是：
> - DataSourceRoutingDataSource.java
> - DataSourceConfigurer.java
> - DynamicDataSourceContextHolder.java
> - DynamicDataSourceAspect.java

---------------------

## 添加依赖
```groovy
dependencies {
    compile('org.mybatis.spring.boot:mybatis-spring-boot-starter:1.3.2')
    compile('org.springframework.boot:spring-boot-starter-web')
    compile('org.springframework.boot:spring-boot-starter-aop')
    runtime('mysql:mysql-connector-java')
    testCompile('org.springframework.boot:spring-boot-starter-test')
}
```

## 创建数据库及表

- 分别创建数据库`product_master`, `product_slave_alpha`, `product_slave_beta`, `product_slave_gamma`
- 在以上数据库中分别创建表 `product`，并插入不同数据

```sql
DROP DATABASE IF EXISTS product_master;
CREATE DATABASE product_master;
CREATE TABLE product_master.product(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  price DOUBLE(10,2) NOT NULL DEFAULT 0);
INSERT INTO product_master.product (name, price) VALUES('master', '1');


DROP DATABASE IF EXISTS product_slave_alpha;
CREATE DATABASE product_slave_alpha;
CREATE TABLE product_slave_alpha.product(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  price DOUBLE(10,2) NOT NULL DEFAULT 0);
INSERT INTO product_slave_alpha.product (name, price) VALUES('slaveAlpha', '1');

DROP DATABASE IF EXISTS product_slave_beta;
CREATE DATABASE product_slave_beta;
CREATE TABLE product_slave_beta.product(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  price DOUBLE(10,2) NOT NULL DEFAULT 0);
INSERT INTO product_slave_beta.product (name, price) VALUES('slaveBeta', '1');

DROP DATABASE IF EXISTS product_slave_gamma;
CREATE DATABASE product_slave_gamma;
CREATE TABLE product_slave_gamma.product(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  price DOUBLE(10,2) NOT NULL DEFAULT 0);
INSERT INTO product_slave_gamma.product (name, price) VALUES('slaveGamma', '1');

```

## 配置数据源

- application.properties

```properties
spring.datasource.type=com.zaxxer.hikari.HikariDataSource
# Master datasource config
spring.datasource.hikari.master.name=master
spring.datasource.hikari.master.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.hikari.master.jdbc-url=jdbc:mysql://localhost/product_master?useSSL=false
spring.datasource.hikari.master.port=3306
spring.datasource.hikari.master.username=root
spring.datasource.hikari.master.password=123456

# SlaveAlpha datasource config
spring.datasource.hikari.slave-alpha.name=SlaveAlpha
spring.datasource.hikari.slave-alpha.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.hikari.slave-alpha.jdbc-url=jdbc:mysql://localhost/product_slave_alpha?useSSL=false
spring.datasource.hikari.slave-alpha.port=3306
spring.datasource.hikari.slave-alpha.username=root
spring.datasource.hikari.slave-alpha.password=123456

# SlaveBeta datasource config
spring.datasource.hikari.slave-beta.name=SlaveBeta
spring.datasource.hikari.slave-beta.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.hikari.slave-beta.jdbc-url=jdbc:mysql://localhost/product_slave_beta?useSSL=false
spring.datasource.hikari.slave-beta.port=3306
spring.datasource.hikari.slave-beta.username=root
spring.datasource.hikari.slave-beta.password=123456

# SlaveGamma datasource config
spring.datasource.hikari.slave-gamma.name=SlaveGamma
spring.datasource.hikari.slave-gamma.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.hikari.slave-gamma.jdbc-url=jdbc:mysql://localhost/product_slave_gamma?useSSL=false
spring.datasource.hikari.slave-gamma.port=3306
spring.datasource.hikari.slave-gamma.username=root
spring.datasource.hikari.slave-gamma.password=123456

spring.aop.proxy-target-class=true
server.port=9999
```

## 配置数据源

- DataSourceKey.java
```java
package cn.com.hellowood.dynamicdatasource.common;

public enum DataSourceKey {
    master,
    slaveAlpha,
    slaveBeta,
    slaveGamma
}

```

- DataSourceRoutingDataSource.java

> 该类继承自 `AbstractRoutingDataSource` 类，在访问数据库时会调用该类的 `determineCurrentLookupKey()` 方法获取数据库实例的 key

```java
package cn.com.hellowood.dynamicdatasource.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected Object determineCurrentLookupKey() {
        logger.info("Current DataSource is [{}]", DynamicDataSourceContextHolder.getDataSourceKey());
        return DynamicDataSourceContextHolder.getDataSourceKey();
    }
}

```

- DataSourceConfigurer.java

> 数据源配置类，在该类中生成多个数据源实例并将其注入到 `ApplicationContext` 中

```java
package cn.com.hellowood.dynamicdatasource.configuration;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfigurer {

    /**
     * master DataSource
     * @Primary 注解用于标识默认使用的 DataSource Bean，因为有5个 DataSource Bean，该注解可用于 master
     * 或 slave DataSource Bean, 但不能用于 dynamicDataSource Bean, 否则会产生循环调用 
     * 
     * @ConfigurationProperties 注解用于从 application.properties 文件中读取配置，为 Bean 设置属性 
     * @return data source
     */
    @Bean("master")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.hikari.master")
    public DataSource master() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Slave alpha data source.
     *
     * @return the data source
     */
    @Bean("slaveAlpha")
    @ConfigurationProperties(prefix = "spring.datasource.hikari.slave-alpha")
    public DataSource slaveAlpha() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Slave beta data source.
     *
     * @return the data source
     */
    @Bean("slaveBeta")
    @ConfigurationProperties(prefix = "spring.datasource.hikari.slave-beta")
    public DataSource slaveBeta() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Slave gamma data source.
     *
     * @return the data source
     */
    @Bean("slaveGamma")
    @ConfigurationProperties(prefix = "spring.datasource.druid.slave-gamma")
    public DataSource slaveGamma() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Dynamic data source.
     *
     * @return the data source
     */
    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource() {
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>(4);
        dataSourceMap.put(DataSourceKey.master.name(), master());
        dataSourceMap.put(DataSourceKey.slaveAlpha.name(), slaveAlpha());
        dataSourceMap.put(DataSourceKey.slaveBeta.name(), slaveBeta());
        dataSourceMap.put(DataSourceKey.slaveGamma.name(), slaveGamma());

        // 将 master 数据源作为默认指定的数据源
        dynamicRoutingDataSource.setDefaultTargetDataSource(master());
        // 将 master 和 slave 数据源作为指定的数据源
        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);

        // 将数据源的 key 放到数据源上下文的 key 集合中，用于切换时判断数据源是否有效
        DynamicDataSourceContextHolder.dataSourceKeys.addAll(dataSourceMap.keySet());

        // 将 Slave 数据源的 key 放在集合中，用于轮循
        DynamicDataSourceContextHolder.slaveDataSourceKeys.addAll(dataSourceMap.keySet());
        DynamicDataSourceContextHolder.slaveDataSourceKeys.remove(DataSourceKey.master.name());
        return dynamicRoutingDataSource;
    }   
    
    /**
     * 配置 SqlSessionFactoryBean
     * @ConfigurationProperties 在这里是为了将 MyBatis 的 mapper 位置和持久层接口的别名设置到 
     * Bean 的属性中，如果没有使用 *.xml 则可以不用该配置，否则将会产生 invalid bond statement 异常
     * 
     * @return the sql session factory bean
     */
    @Bean
    @ConfigurationProperties(prefix = "mybatis")
    public SqlSessionFactoryBean sqlSessionFactoryBean() {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        // 配置 MyBatis
        sqlSessionFactoryBean.setTypeAliasesPackage("cn.com.hellowood.dynamicdatasource.mapper");
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("mappers/**Mapper.xml"));

        // 配置数据源，此处配置为关键配置，如果没有将 dynamicDataSource 作为数据源则不能实现切换
        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
        return sqlSessionFactoryBean;
    }
    
    /**
     * 注入 DataSourceTransactionManager 用于事务管理
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dynamicDataSource());
    }
    
}

```

- DynamicDataSourceContextHolder.java

> 该类为数据源上下文配置，用于切换数据源

```java
package cn.com.hellowood.dynamicdatasource.configuration;


import cn.com.hellowood.dynamicdatasource.common.DataSourceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DynamicDataSourceContextHolder {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);

    /**
     * 用于轮循的计数器
     */
    private static int counter = 0;

    /**
     * Maintain variable for every thread, to avoid effect other thread
     */
    private static final ThreadLocal<Object> CONTEXT_HOLDER = ThreadLocal.withInitial(DataSourceKey.master);


    /**
     * All DataSource List
     */
    public static List<Object> dataSourceKeys = new ArrayList<>();

    /**
     * The constant slaveDataSourceKeys.
     */
    public static List<Object> slaveDataSourceKeys = new ArrayList<>();

    /**
     * To switch DataSource
     *
     * @param key the key
     */
    public static void setDataSourceKey(String key) {
        CONTEXT_HOLDER.set(key);
    }

    /**
     * Use master data source.
     */
    public static void useMasterDataSource() {
        CONTEXT_HOLDER.set(DataSourceKey.master);
    }

    /**
     * 当使用只读数据源时通过轮循方式选择要使用的数据源
     */
    public static void useSlaveDataSource() {

        try {
            int datasourceKeyIndex = counter % slaveDataSourceKeys.size();
            CONTEXT_HOLDER.set(String.valueOf(slaveDataSourceKeys.get(datasourceKeyIndex)));
            counter++;
        } catch (Exception e) {
            logger.error("Switch slave datasource failed, error message is {}", e.getMessage());
            useMasterDataSource();
            e.printStackTrace();
        } 
    }

    /**
     * Get current DataSource
     *
     * @return data source key
     */
    public static String getDataSourceKey() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * To set DataSource as default
     */
    public static void clearDataSourceKey() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * Check if give DataSource is in current DataSource list
     *
     * @param key the key
     * @return boolean boolean
     */
    public static boolean containDataSourceKey(String key) {
        return dataSourceKeys.contains(key);
    }
}


```

- DynamicDataSourceAspect.java

> 动态数据源切换的切面，切 DAO 层，通过 DAO 层方法名判断使用哪个数据源，实现数据源切换
> 关于切面的 Order 可以可以不设，因为 `@Transactional` 是最低的，取决于其他切面的设置，并且在 `org.springframework.core.annotation.AnnotationAwareOrderComparator` 会重新排序

```java
package cn.com.hellowood.dynamicdatasource.configuration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DynamicDataSourceAspect {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    private final String[] QUERY_PREFIX = {"select"};

    @Pointcut("execution( * cn.com.hellowood.dynamicdatasource.mapper.*.*(..))")
    public void daoAspect() {
    }

    @Before("daoAspect()")
    public void switchDataSource(JoinPoint point) {
        Boolean isQueryMethod = isQueryMethod(point.getSignature().getName());
        if (isQueryMethod) {
            DynamicDataSourceContextHolder.useSlaveDataSource();
            logger.info("Switch DataSource to [{}] in Method [{}]",
                    DynamicDataSourceContextHolder.getDataSourceKey(), point.getSignature());
        }
    }

    @After("daoAspect()")
    public void restoreDataSource(JoinPoint point) {
        DynamicDataSourceContextHolder.clearDataSourceKey();
        logger.info("Restore DataSource to [{}] in Method [{}]",
                DynamicDataSourceContextHolder.getDataSourceKey(), point.getSignature());
    }

    private Boolean isQueryMethod(String methodName) {
        for (String prefix : QUERY_PREFIX) {
            if (methodName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

}

```


## 配置 Product REST API 接口

- ProductController.java
   
```java
package cn.com.hellowood.dynamicdatasource.controller;

import cn.com.hellowood.dynamicdatasource.common.CommonResponse;
import cn.com.hellowood.dynamicdatasource.common.ResponseUtil;
import cn.com.hellowood.dynamicdatasource.modal.Product;
import cn.com.hellowood.dynamicdatasource.service.ProductService;
import cn.com.hellowood.dynamicdatasource.utils.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public CommonResponse getProduct(@PathVariable("id") Long productId) throws ServiceException {
        return ResponseUtil.generateResponse(productService.select(productId));
    }

    @GetMapping
    public CommonResponse getAllProduct() {
        return ResponseUtil.generateResponse(productService.getAllProduct());
    }

    @PutMapping("/{id}")
    public CommonResponse updateProduct(@PathVariable("id") Long productId, @RequestBody Product newProduct) throws ServiceException {
        return ResponseUtil.generateResponse(productService.update(productId, newProduct));
    }

    @DeleteMapping("/{id}")
    public CommonResponse deleteProduct(@PathVariable("id") long productId) throws ServiceException {
        return ResponseUtil.generateResponse(productService.delete(productId));
    }

    @PostMapping
    public CommonResponse addProduct(@RequestBody Product newProduct) throws ServiceException {
        return ResponseUtil.generateResponse(productService.add(newProduct));
    }
}


```

- ProductService.java
```java
package cn.com.hellowood.dynamicdatasource.service;

import cn.com.hellowood.dynamicdatasource.mapper.ProductDao;
import cn.com.hellowood.dynamicdatasource.modal.Product;
import cn.com.hellowood.dynamicdatasource.utils.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductDao productDao;

    public Product select(long productId) throws ServiceException {
        Product product = productDao.select(productId);
        if (product == null) {
            throw new ServiceException("Product:" + productId + " not found");
        }
        return product;
    }

    @Transactional(rollbackFor = DataAccessException.class)
    public Product update(long productId, Product newProduct) throws ServiceException {

        if (productDao.update(newProduct) <= 0) {
            throw new ServiceException("Update product:" + productId + "failed");
        }
        return newProduct;
    }

    @Transactional(rollbackFor = DataAccessException.class)
    public boolean add(Product newProduct) throws ServiceException {
        Integer num = productDao.insert(newProduct);
        if (num <= 0) {
            throw new ServiceException("Add product failed");
        }
        return true;
    }

    @Transactional(rollbackFor = DataAccessException.class)
    public boolean delete(long productId) throws ServiceException {
        Integer num = productDao.delete(productId);
        if (num <= 0) {
            throw new ServiceException("Delete product:" + productId + "failed");
        }
        return true;
    }

    public List<Product> getAllProduct() {
        return productDao.getAllProduct();
    }
}

```

- ProductDao.java

```java
package cn.com.hellowood.dynamicdatasource.mapper;

import cn.com.hellowood.dynamicdatasource.modal.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductDao {
    Product select(@Param("id") long id);

    Integer update(Product product);

    Integer insert(Product product);

    Integer delete(long productId);

    List<Product> getAllProduct();
}

```

- ProductMapper.xml

> 启动项目，此时访问 `/product/1` 会返回 `product_master` 数据库中 `product` 表中的所有数据，多次访问 `/product` 会分别返回 `product_slave_alpha`、`product_slave_beta`、`product_slave_gamma` 数据库中 `product` 表中的数据，同时也可以在看到切换数据源的 log，说明动态切换数据源是有效的

---------------

## 注意

> 在该应用中因为使用了 DAO 层的切面切换数据源，所以 `@Transactional` 注解不能加在类上，只能用于方法；有 `@Trasactional`注解的方法无法切换数据源
