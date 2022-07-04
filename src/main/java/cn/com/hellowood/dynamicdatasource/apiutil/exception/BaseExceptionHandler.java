package cn.com.hellowood.dynamicdatasource.apiutil.exception;

import cn.com.hellowood.dynamicdatasource.apiutil.model.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.com.hellowood.dynamicdatasource.apiutil.exception.enums.CustomExceptionEnum.PARAM_ERROR;

/**
 * 错误处理句柄
 *
 * @author LDZ
 * @date 2020-03-02 17:19
 */

@ControllerAdvice
public class BaseExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BaseExceptionHandler.class);

    /**
     * @param request  请求
     * @param response 返回
     * @param handler  句柄
     * @param ex       错误
     * @return 统一封装返回值
     */
    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public BaseResponse argumentMissingError(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return PARAM_ERROR.handlerBaseResponse("参数错误");
    }

    @ResponseBody
    @ExceptionHandler(BindException.class)
    public BaseResponse bindError(HttpServletRequest request, HttpServletResponse response, Object handler, BindException ex) {
        String errMessage = ex.getFieldErrors().stream().map(fieldError -> fieldError.getField() + ":" + fieldError.getDefaultMessage()).collect(Collectors.joining(","));
        log.warn("server param error ", ex);
        return PARAM_ERROR.handlerBaseResponse(errMessage);

    }


    /**
     * 抓取所有的错误
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public BaseResponse defaultErrorHandle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        log.error("server error ", ex);
        return PARAM_ERROR.handlerBaseResponse("服务器繁忙");
    }


    @ResponseBody
    @ExceptionHandler(CustomServiceException.class)
    public BaseResponse customExceptionHandle(HttpServletRequest request, HttpServletResponse response, Object handler, CustomServiceException customServiceException) {

        log.debug("business err {} ", customServiceException.getErrorDescription());
        return customServiceException.getCustomExceptionEnum().handlerBaseResponse(
                Optional.ofNullable(customServiceException.getErrorDescription()).orElse("服务器繁忙"),
                Optional.ofNullable(customServiceException.getData()).orElse(null));


    }

}
