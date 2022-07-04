package cn.com.hellowood.dynamicdatasource.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * For handler not expected status
 *
 * @author HelloWood
 * @date 2017-07-11 12:18
 * @Email hellowoodes@gmail.com
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ServiceException extends Exception {

    public ServiceException(String msg, Exception e) {
        super(msg + "\n" + e.getMessage());
    }

    public ServiceException(String msg) {
        super(msg);
    }
}
