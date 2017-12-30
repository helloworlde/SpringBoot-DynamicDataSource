package cn.com.hellowood.dynamicdatasource.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * @author HelloWood
 * @date 2017-07-30 22:26
 * @email hellowoodes@gmail.com
 **/

@Aspect
@Component
public class HttpLog {

    public static final Logger logger = LoggerFactory.getLogger(HttpLog.class);

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(public * cn.com.hellowood.dynamicdatasource.controller.*.*(..))")
    public void httpLog() {

    }

    @Before("httpLog()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //URL
        logger.debug("url:{}", request.getRequestURL());
        //Request method
        logger.debug("method:{}", request.getMethod());
        //IP
        logger.debug("ip:{}", request.getRemoteAddr());
        //Class method name
        logger.debug("method:{}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        // Argument
        logger.debug("args:{}", JSONUtil.toJSONString(joinPoint.getArgs()));
    }

    @After("httpLog()")
    public void doAfter() {
        logger.debug("request cost time:{} ms", System.currentTimeMillis() - startTime.get());
    }

    @AfterReturning(returning = "object", pointcut = "httpLog()")
    public void afterReturning(Object object) {
        logger.debug("response:{}", JSONUtil.toJSONString(object));
    }
}
