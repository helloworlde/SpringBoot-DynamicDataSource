package cn.com.hellowood.dynamicdatasource.apiutil.annotation;

import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.annotation.*;

/**
 * 会在拦截器那里判断是否有这个注解，如果存在把结果包装成 {code:'',data:''} 的形式
 *
 * @author LDZ
 * @date 2020-03-02 16:36
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiResponseBody {
    /**
     * 序列化可选 fastjson
     *
     * @return
     * @see SerializerFeature
     */
    SerializerFeature[] serializerFeature() default {};
}
