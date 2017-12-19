package cn.com.hellowood.dynamicdatasource.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;


/**
 * Config for application
 *
 * @author HelloWood
 * @date 2017-07-11 21:35
 * @Email hellowoodes@gmail.com
 */

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

    private final Logger logger = LoggerFactory.getLogger(WebMvcConfigurer.class);

    /**
     * Exception resolver method
     *
     * @param exceptionResolvers
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new CustomHandlerExceptionResolver());
    }

}
