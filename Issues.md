# 在使用 Spring Boot 和 MyBatis 动态切换数据源时遇到的问题以及解决方法

> 相关项目地址:[https://github.com/helloworlde/SpringBoot-DynamicDataSource](https://github.com/helloworlde/SpringBoot-DynamicDataSource)

## 1. org.apache.ibatis.binding.BindingException: Invalid bound statement (not found)

> 在使用了动态数据源后遇到了该问题，从错误信息来看是因为没有找到 `*.xml` 文件而导致的，但是在配置文件中
确实添加了相关的配置，这种错误的原因是因为设置数据源后没有设置`SqlSessionFactoryBean`的 `typeAliasesPackage`
和`mapperLocations`属性或属性无效导致的；

- 解决方法：

> 如果在应用的入口类中添加了 `@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)`,
在`DataSourceConfigure`类的中设置相关属性：

```java
    @Bean
    @ConfigurationProperties(prefix = "mybatis")
    public SqlSessionFactoryBean sqlSessionFactoryBean() {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
        return sqlSessionFactoryBean;
    }
```

或者直接配置(不推荐该方式)：

```java
    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean() {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setTypeAliasesPackage("typeAliasesPackage");
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("mapperLocations"));
        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
        return sqlSessionFactoryBean;
    }
```





## 2. Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, or using @Qualifier to identify the bean that should be consumed

> 该异常在错误信息中已经说的很清楚了，是因为有多个 `DataSource` 的实例，所以无法确定该引用那个实例

- 解决方法：

> 为数据源的某个 `Bean` 添加 `@Primary` 注解，该 `Bean` 应当是通过 `DataSourceBuilder.create().build()`
得到的 `Bean`，而不是通过 `new AbstractRoutingDataSource` 的子类实现的 `Bean`，在本项目中可以是 `master()`
或 `slave()` 得到的 `DataSource`，不能是 `dynamicDataSource()` 得到的 `DataSource`

## 3. 通过注解方式动态切换数据源无效

- 请确认注解没有放到 DAO 层方法上, 因为会在 Service 层开启事务，所以当注解在 DAO 层时不会生效
- 请确认以下 `Bean` 正确配置：

```java
    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource() {
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>(2);
        dataSourceMap.put("master", master());
        dataSourceMap.put("slave", slave());

        // Set master datasource as default
        dynamicRoutingDataSource.setDefaultTargetDataSource(master());
        // Set master and slave datasource as target datasource
        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);

        // To put datasource keys into DataSourceContextHolder to judge if the datasource is exist
        DynamicDataSourceContextHolder.dataSourceKeys.addAll(dataSourceMap.keySet());
        return dynamicRoutingDataSource;
    }

    @Bean
    @ConfigurationProperties(prefix = "mybatis")
    public SqlSessionFactoryBean sqlSessionFactoryBean() {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        // Here is very important, if don't config this, will can't switch datasource
        // put all datasource into SqlSessionFactoryBean, then will autoconfig SqlSessionFactory
        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
        return sqlSessionFactoryBean;
    }

```

## 4. `@Transactional` 注解无效，发生异常不回滚

- 请确认该 `Bean` 得到正确配置，并且`@Transactional` 的 `rollbackFor` 配置正确

```java
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dynamicDataSource());
    }
    
```

## 5. 通过 AOP 判断 DAO 层方法名时切换数据源无效

> 当切面指向了 DAO 层后无论如何设置切面的顺序，都无法在执行查询之前切换数据源，但是切面改为 Service 层后可以正常工作

- 解决方法: 请确认 `@Transactional` 注解是加在方法上而不是 Service 类上，添加了 `@Transactional` 的方法因为在 Service 层开启了事务，
会在事务结束之后才会切换数据源

- 检出 `DataSourceTransactionManager` Bean 注入正确

```java
    @Bean 
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dynamicDataSource());
    }
```

## 6. The dependencies of some of the beans in the application context form a cycle

- 错误信息：

```
The dependencies of some of the beans in the application context form a cycle:

   produceController (field private cn.com.hellowood.dynamicdatasource.service.ProductService cn.com.hellowood.dynamicdatasource.controller.ProduceController.productService)
      ↓
   productService (field private cn.com.hellowood.dynamicdatasource.mapper.ProductDao cn.com.hellowood.dynamicdatasource.service.ProductService.productDao)
      ↓
   productDao defined in file [/Users/hellowoodes/Downloads/Dev/SpringBoot/DynamicDataSource/out/production/classes/cn/com/hellowood/dynamicdatasource/mapper/ProductDao.class]
      ↓
   sqlSessionFactoryBean defined in class path resource [cn/com/hellowood/dynamicdatasource/configuration/DataSourceConfigurer.class]
┌─────┐
|  dynamicDataSource defined in class path resource [cn/com/hellowood/dynamicdatasource/configuration/DataSourceConfigurer.class]
↑     ↓
|  master defined in class path resource [cn/com/hellowood/dynamicdatasource/configuration/DataSourceConfigurer.class]
↑     ↓
|  dataSourceInitializer
└─────┘
```

> 这是因为在注入 `DataSource` 的实例的时候产生了循环调用，第一个注入的 Bean 依赖于其他的 Bean， 而被依赖的 Bean 产生依赖传递，依赖第一个
注入的 Bean, 陷入了循环，无法启动项目

- 解决方法：将 `@Primary` 注解指向没有依赖的 Bean，如：
```java

    /**
     * master DataSource
     * @Primary 注解用于标识默认使用的 DataSource Bean，因为有三个 DataSource Bean，该注解可用于 master
     * 或 slave DataSource Bean, 但不能用于 dynamicDataSource Bean, 否则会产生循环调用 
     * 
     * @ConfigurationProperties 注解用于从 application.properties 文件中读取配置，为 Bean 设置属性 
     * @return data source
     */
    @Bean("master")
    @Primary
    @ConfigurationProperties(prefix = "application.server.db.master")
    public DataSource master() {
        return DataSourceBuilder.create().build();
    }

    @Bean("slave")
    @ConfigurationProperties(prefix = "application.server.db.slave")
    public DataSource slave() {
        return DataSourceBuilder.create().build();
    }

    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource() {
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
        Map<Object, Object> dataSourceMap = new HashMap<>(2);
        dataSourceMap.put("master", master());
        dataSourceMap.put("slave", slave());

        // Set master datasource as default
        dynamicRoutingDataSource.setDefaultTargetDataSource(master());
        // Set master and slave datasource as target datasource
        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);

        // To put datasource keys into DataSourceContextHolder to judge if the datasource is exist
        DynamicDataSourceContextHolder.dataSourceKeys.addAll(dataSourceMap.keySet());
        return dynamicRoutingDataSource;
    }
```
