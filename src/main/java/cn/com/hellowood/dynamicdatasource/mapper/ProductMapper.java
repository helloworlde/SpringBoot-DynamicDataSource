package cn.com.hellowood.dynamicdatasource.mapper;

import cn.com.hellowood.dynamicdatasource.modal.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Product mapper for operate data of products table
 *
 * @Date 2017-07-11 10:54
 * @Author HelloWood
 * @Email hellowoodes@gmail.com
 */

@Mapper
public interface ProductMapper {
    Product select(@Param("id") long id);

    Integer update(Product product);

    Integer insert(Product product);

    Integer delete(long productId);

    List<Product> selectAll();
}
