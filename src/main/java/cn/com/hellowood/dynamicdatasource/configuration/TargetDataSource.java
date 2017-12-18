package cn.com.hellowood.dynamicdatasource.configuration;

import java.lang.annotation.*;

/**
 * Multiple DataSource Aspect For Switch DataSource
 *
 * @Date 2017-08-15 14:36
 * @Author HelloWood
 * @Email hellowoodes@gmail.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String value();
}
