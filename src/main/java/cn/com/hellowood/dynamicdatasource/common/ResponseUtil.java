package cn.com.hellowood.dynamicdatasource.common;

import cn.com.hellowood.dynamicdatasource.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Generate response for request
 *
 * @author HelloWood
 * @date 2017-07-11 15:45
 * @Email hellowoodes@gmail.com
 */
public class ResponseUtil {

    private static final Logger logger = LoggerFactory.getLogger(ResponseUtil.class);

    /**
     * Handler response information
     *
     * @param response
     * @param object
     * @return
     */
    public static HttpServletResponse handlerResponse(HttpServletResponse response, Object object) {
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSONUtil.toJSONString(object));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return response;
    }
}
