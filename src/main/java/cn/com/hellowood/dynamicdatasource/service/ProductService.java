package cn.com.hellowood.dynamicdatasource.service;

import cn.com.hellowood.dynamicdatasource.mapper.ProductMapper;
import cn.com.hellowood.dynamicdatasource.modal.Product;
import cn.com.hellowood.dynamicdatasource.utils.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Product service for handler logic of product operation
 *
 * @Date 2017-07-11 11:58
 * @Author HelloWood
 * @Email hellowoodes@gmail.com
 */

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    /**
     * Get product by id
     * If not found product will throw ServiceException
     *
     * @param productId
     * @return
     * @throws ServiceException
     */
    public Product select(long productId) throws ServiceException {
        Product product = productMapper.select(productId);
        if (product == null) {
            throw new ServiceException("Product:" + productId + " not found");
        }
        return product;
    }

    /**
     * Update product by id
     * If update failed will throw ServiceException
     *
     * @param productId
     * @param newProduct
     * @return
     * @throws ServiceException
     */
    public Product update(long productId, Product newProduct) throws ServiceException {

        if (productMapper.update(newProduct) <= 0) {
            throw new ServiceException("Update product:" + productId + "failed");
        }
        return newProduct;
    }

    /**
     * Add product to DB
     *
     * @param newProduct
     * @return
     * @throws ServiceException
     */
    public boolean add(Product newProduct) throws ServiceException {
        Integer num = productMapper.insert(newProduct);
        if (num <= 0) {
            throw new ServiceException("Add product failed");
        }
        return true;
    }

    /**
     * Delete product from DB
     *
     * @param productId
     * @return
     * @throws ServiceException
     */
    public boolean delete(long productId) throws ServiceException {
        Integer num = productMapper.delete(productId);
        if (num <= 0) {
            throw new ServiceException("Delete product:" + productId + "failed");
        }
        return true;
    }

    /**
     * Query all product
     *
     * @return
     */
    public List<Product> selectAll() {
        return productMapper.selectAll();
    }
}
