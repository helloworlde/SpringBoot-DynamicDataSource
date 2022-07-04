package cn.com.hellowood.dynamicdatasource.apiutil.interceptor;

import cn.com.hellowood.dynamicdatasource.apiutil.annotation.ApiResponseBody;
import cn.com.hellowood.dynamicdatasource.apiutil.model.BaseResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;

/**
 * api 层返回值处理句柄
 *
 * @author LDZ
 * @date 2020-03-02 16:43
 */
public class ApiResponseBodyReturnValueHandler implements HandlerMethodReturnValueHandler, AsyncHandlerMethodReturnValueHandler {
    /**
     * 处理的返回类型
     *
     * @param returnType 返回类型
     * @return true 处理  false 不处理
     */
    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        // 如果已经是基础的返回值
        return returnType.getParameterType() != ApiResponseBody.class
                && (returnType.getAnnotatedElement().getAnnotation(ApiResponseBody.class) != null);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        mavContainer.setRequestHandled(true);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        assert response != null;
        response.setContentType("application/json;charset=utf-8");
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(100);
        baseResponse.setMessage("成功");
        baseResponse.setData(returnValue);

        ApiResponseBody apiResponseBody = returnType.getAnnotatedElement().getAnnotation(ApiResponseBody.class);

        SerializerFeature[] defaultSerializerFeature = {
                SerializerFeature.DisableCircularReferenceDetect
        };

        if (apiResponseBody != null && apiResponseBody.serializerFeature().length != 0) {
            defaultSerializerFeature = apiResponseBody.serializerFeature();
        }
        response.getWriter().write(JSON.toJSONString(baseResponse, defaultSerializerFeature));

    }

    @Override
    public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
        return supportsReturnType(returnType);
    }
}
