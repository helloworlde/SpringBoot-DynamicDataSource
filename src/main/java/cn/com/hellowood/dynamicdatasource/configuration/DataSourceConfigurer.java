package cn.com.hellowood.dynamicdatasource.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Multiple DataSource Configurer
 *
 * @Date 2017-08-15 11:37
 * @Author HelloWood
 * @Email hellowoodes@gmail.com
 */
@Configuration
public class DataSourceConfigurer {

    /**
     * master DataSource
     *
     * @return
     */
    @Bean("master")
    @Qualifier("master")
    @Primary
    @ConfigurationProperties(prefix = "application.server.db.master")
    public DataSource master() {
        return DataSourceBuilder.create().build();
    }

    /**
     * slave DataSource
     *
     * @return
     */
    @Bean("slave")
    @Qualifier("slave")
    @ConfigurationProperties(prefix = "application.server.db.slave")
    public DataSource slave() {
        return DataSourceBuilder.create().build();
    }

    @Bean("dynamicDataSource")
    @Qualifier("dynamicDataSource")
    public DataSource dataSource() {
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();

        dynamicRoutingDataSource.setDefaultTargetDataSource(slave());

        Map<Object, Object> dataSourceMap = new HashMap<>(2);
        dataSourceMap.put("master", master());
        dataSourceMap.put("slave", slave());
        dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);
        DynamicDataSourceContextHolder.dataSourceIds.addAll(dataSourceMap.keySet());
        return dynamicRoutingDataSource;
    }
//
//    @Bean
//    public PlatformTransactionManager transactionManager(DataSource dynamicDataSource) {
//        return new DataSourceTransactionManager(dynamicDataSource);
//    }
//
//    @Bean
//    @ConfigurationProperties(prefix = "mybatis")
//    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dynamicDataSource) {
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dynamicDataSource);
//        return sqlSessionFactoryBean;
//    }
//
//    @Bean
//    public SqlSessionFactory sqlSessionFactory() throws Exception {
//        return sqlSessionFactoryBean(dataSource()).getObject();
////        return sqlSessionFactoryBean.getObject();
//    }
//
//    @Bean
//    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
//        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
//        return sqlSessionTemplate;
//    }
}

