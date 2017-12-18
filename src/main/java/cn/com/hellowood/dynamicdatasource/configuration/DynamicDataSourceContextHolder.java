package cn.com.hellowood.dynamicdatasource.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple DataSource Context Holder
 *
 * @Date 2017-08-15 14:26
 * @Author HelloWood
 * @Email hellowoodes@gmail.com
 */
//@Component
public class DynamicDataSourceContextHolder {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceContextHolder.class);

    /**
     * Maintain variable for every thread, to avoid effect other thread
     */
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    /**
     * All DataSource List
     */
    public static List<Object> dataSourceIds = new ArrayList<>();

    /**
     * To switch DataSource
     *
     * @param key
     */
    public static void setDataSourceKey(String key) {
        contextHolder.set(key);
    }

    /**
     * Get current DataSource
     *
     * @return
     */
    public static String getDataSourceKey() {
        return contextHolder.get();
    }

    /**
     * To set DataSource as default
     */
    public static void clearDataSourceKey() {
        contextHolder.remove();
    }

    /**
     * Check if give DataSource is in current DataSource list
     *
     * @param key
     * @return
     */
    public static boolean containDataSourceKey(String key) {
        logger.info("DataSourceIds is {}", dataSourceIds);
        return dataSourceIds.contains(key);
    }
}
