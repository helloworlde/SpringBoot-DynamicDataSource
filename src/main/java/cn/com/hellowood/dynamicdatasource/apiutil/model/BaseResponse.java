package cn.com.hellowood.dynamicdatasource.apiutil.model;

import cn.com.hellowood.dynamicdatasource.apiutil.exception.enums.CustomExceptionEnum;

import java.io.Serializable;

/**
 * 统一返回标志
 *
 * @author LDZ
 * @date 2020-03-02 16:48
 */
public class BaseResponse implements Serializable {

    private static final long serialVersionUID = -6818493817970279447L;
    /**
     * 返回code码
     */
    private int code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回数据
     */
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


    public BaseResponse() {
    }

    public BaseResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(CustomExceptionEnum customExceptionEnum, String message, Object data) {
        this.code = customExceptionEnum.getCode();
        this.message = message;
        this.data = data;
    }

    public BaseResponse(CustomExceptionEnum customExceptionEnum, String message) {
        this.code = customExceptionEnum.getCode();
        this.message = message;
    }

}
