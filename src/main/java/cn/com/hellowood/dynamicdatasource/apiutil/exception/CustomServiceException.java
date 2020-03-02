package cn.com.hellowood.dynamicdatasource.apiutil.exception;

/**
 * @author XiaoLei
 * @date 2018/4/17 15:19
 * @description
 */

import cn.com.hellowood.dynamicdatasource.apiutil.exception.enums.CustomExceptionEnum;

/**
 * 自定义异常
 *
 * @author LDZ
 * @date 2020-03-02 17:22
 */
public class CustomServiceException extends RuntimeException {

    /**
     * 自定义错误
     */
    private CustomExceptionEnum customExceptionEnum;

    /**
     * 错误的描述
     */
    private String errorDescription;

    /**
     * 需要返回的数据
     */
    private Object data;


    public CustomServiceException(CustomExceptionEnum customExceptionEnum, String desc) {
        super(desc);
        this.customExceptionEnum = customExceptionEnum;
        this.errorDescription = desc;
    }

    public CustomServiceException(CustomExceptionEnum customExceptionEnum, String desc, Object data) {
        super(desc);
        this.customExceptionEnum = customExceptionEnum;
        this.errorDescription = desc;
        this.data = data;
    }

    public CustomExceptionEnum getCustomExceptionEnum() {
        return customExceptionEnum;
    }

    public void setCustomExceptionEnum(CustomExceptionEnum customExceptionEnum) {
        this.customExceptionEnum = customExceptionEnum;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
