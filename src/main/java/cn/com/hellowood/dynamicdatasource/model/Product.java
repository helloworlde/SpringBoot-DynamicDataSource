package cn.com.hellowood.dynamicdatasource.model;

import java.io.Serializable;

/**
 * Product bean
 *
 * @author HelloWood
 * @date 2017-07-11 11:09
 * @Email hellowoodes@gmail.com
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1435515995276255188L;

    private long id;
    private String name;
    private long price;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
