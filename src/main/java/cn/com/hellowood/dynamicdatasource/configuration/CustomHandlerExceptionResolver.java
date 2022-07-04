package cn.com.hellowood.dynamicdatasource.configuration;

import cn.com.hellowood.dynamicdatasource.common.CommonConstant;
import cn.com.hellowood.dynamicdatasource.common.CommonResponse;
import cn.com.hellowood.dynamicdatasource.common.ResponseCode;
import cn.com.hellowood.dynamicdatasource.common.ResponseUtil;
import cn.com.hellowood.dynamicdatasource.error.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Custom exception handler
 *
 * @author HelloWood
 * @date 2017-07-11 21:03
 * @Email hellowoodes@gmail.com
 */
public class CustomHandlerExceptionResolver implements HandlerExceptionResolver {
    private final Logger logger = LoggerFactory.getLogger(CustomHandlerExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        CommonResponse commonResponse = new CommonResponse();
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //Service exception,handler exception from service
            if (ex instanceof ServiceException) {
                commonResponse.setCode(ResponseCode.SUCCESS).setMessage(ex.getMessage());
                logger.warn(ex.getMessage());
            } else {
                //DB exception
                if (ex instanceof DataAccessException) {
                    commonResponse.setCode(ResponseCode.INTERNAL_SERVER_ERROR)
                            .setMessage(CommonConstant.DB_ERROR_MESSAGE);
                } else {
                    //Others exception
                    commonResponse.setCode(ResponseCode.INTERNAL_SERVER_ERROR)
                            .setMessage(CommonConstant.SERVER_ERROR_MESSAGE);
                }

                // error message detail
                String message = String.format("interface [%s] has exception,method is %s.%s, exception message is %s",
                        request.getRequestURI(),
                        handlerMethod.getBean().getClass().getName(),
                        handlerMethod.getMethod().getName(),
                        ex.getMessage());

                logger.error(message, ex);
            }
        } else {
            if (ex instanceof NoHandlerFoundException) {
                commonResponse.setCode(ResponseCode.NOT_FOUND).setMessage("interface [" + request.getRequestURI() + "] not exist");
            } else {
                commonResponse.setCode(ResponseCode.INTERNAL_SERVER_ERROR).setMessage(ex.getMessage());
                logger.error(ex.getMessage(), ex);
            }
        }

        ResponseUtil.handlerResponse(response, commonResponse);
        return new ModelAndView();
    }
}
