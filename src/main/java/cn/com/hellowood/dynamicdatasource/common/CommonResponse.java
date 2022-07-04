package cn.com.hellowood.dynamicdatasource.common;


import cn.com.hellowood.dynamicdatasource.utils.JSONUtil;

/**
 * Response bean for format response
 *
 * @author HelloWood
 * @date 2017-07-11 15:33
 * @Email hellowoodes@gmail.com
 */
public class CommonResponse {

    private int code;
    private String message;
    private Object data;

    public int getCode() {
        return code;
    }

    public CommonResponse setCode(ResponseCode responseCode) {
        this.code = responseCode.code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CommonResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public CommonResponse setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}
