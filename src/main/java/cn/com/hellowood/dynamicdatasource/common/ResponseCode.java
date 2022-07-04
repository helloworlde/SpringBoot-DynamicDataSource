package cn.com.hellowood.dynamicdatasource.common;

/**
 * Response code
 *
 * @author HelloWood
 * @date 2017-07-11 15:41
 * @Email hellowoodes@gmail.com
 */
public enum ResponseCode {
    SUCCESS(200),
    FAIL(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    public int code;

    ResponseCode(int code) {
        this.code = code;
    }
}
