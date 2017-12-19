package cn.com.hellowood.dynamicdatasource.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Base controller
 *
 * @author HelloWood
 * @date 2017-09-25 13:31
 * @Email hellowoodes@gmail.com
 */

@RestController
public class BaseController {

    /**
     * Root path, The HEAD method is for SpringBoot Admin to monitor application status
     *
     * @return
     */
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.HEAD})
    @ResponseBody
    public String root() {
        return "Hello World";
    }
}
