package cn.com.hellowood.dynamicdatasource.configuration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Multiple DataSource Aspect
 *
 * @Date 2017-08-15 11:37
 * @Author HelloWood
 * @Email hellowoodes@gmail.com
 */
@Aspect
@Order(-1) // To ensure execute before @Transactional
@Component
public class DynamicDataSourceAspect {
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    /**
     * Switch DataSource
     *
     * @param point
     * @param targetDataSource
     */
    @Before("@annotation(targetDataSource))")
    public void switchDataSource(JoinPoint point, TargetDataSource targetDataSource) {
        if (!DynamicDataSourceContextHolder.containDataSourceKey(targetDataSource.value())) {
            logger.error("DataSource [{}] doesn't exist, use default DataSource [{}]",
                    targetDataSource.value(), point.getSignature());
        } else {
            logger.info("Current DataSource is [{}]", DynamicDataSourceContextHolder.getDataSourceKey());
            logger.info("Switch DataSource to [{}] in Method [{}]",
                    targetDataSource.value(), point.getSignature());
            DynamicDataSourceContextHolder.setDataSourceKey(targetDataSource.value());
        }
    }

    /**
     * Restore DataSource
     *
     * @param point
     * @param targetDataSource
     */
    @After("@annotation(targetDataSource))")
    public void restoreDataSource(JoinPoint point, TargetDataSource targetDataSource) {
        logger.info("Restore DataSource to [{}] in Method [{}]",
                targetDataSource.value(), point.getSignature());
        DynamicDataSourceContextHolder.clearDataSourceKey();
    }

}
