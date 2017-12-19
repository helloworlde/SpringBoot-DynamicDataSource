package cn.com.hellowood.dynamicdatasource.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;


/**
 * JSON data util, for parse and generate JSON data
 *
 * @author HelloWood
 * @date 2017-07-11 16:11
 * @Email hellowoodes@gmail.com
 */


public class JSONUtil {

    private static final Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    /**
     * Transfer object to JSON string
     *
     * @param object
     * @return
     */
    public static String toJSONString(Object object) {
        String result = null;
        ObjectMapper objectMapper = new ObjectMapper();
        //set config of JSON
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);// can use single quote
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);//allow unquoted field names
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));//set date format

        try {
            result = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Generate JSON String error!" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
