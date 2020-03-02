package cn.com.hellowood.dynamicdatasource.controller;

import cn.com.hellowood.dynamicdatasource.apiutil.annotation.ApiResponseBody;
import cn.com.hellowood.dynamicdatasource.model.Product;
import cn.com.hellowood.dynamicdatasource.service.ProductService;
import cn.com.hellowood.dynamicdatasource.error.ServiceException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Product controller
 *
 * @author HelloWood
 * @date 2017-07-11 11:38
 * @Email hellowoodes@gmail.com
 */

@RestController
@RequestMapping("/product")
public class ProductController {

    @Resource
    private ProductService productService;

    /**
     * Get product by id
     *
     * @param productId
     * @return
     * @throws ServiceException
     */
    @GetMapping("/{id}")
    @ApiResponseBody
    public Product getProduct(@PathVariable("id") Long productId) throws ServiceException {
        return productService.select(productId);
    }

    /**
     * Get all product
     *
     * @return
     * @throws ServiceException
     */
    @GetMapping
    @ApiResponseBody
    public List<Product> getAllProduct() {
        return productService.getAllProduct();
    }

    /**
     * Update product by id
     *
     * @param productId
     * @param newProduct
     * @return
     * @throws ServiceException
     */

    @PutMapping("/{id}")
    @ApiResponseBody
    public Product updateProduct(@PathVariable("id") Long productId, @RequestBody Product newProduct) throws ServiceException {
        return productService.update(productId, newProduct);
    }

    /**
     * Delete product by id
     *
     * @param productId
     * @return
     * @throws ServiceException
     */
    @DeleteMapping("/{id}")
    @ApiResponseBody
    public boolean deleteProduct(@PathVariable("id") long productId) throws ServiceException {
        return productService.delete(productId);
    }

    /**
     * Save product
     *
     * @param newProduct
     * @return
     * @throws ServiceException
     */
    @PostMapping
    @ApiResponseBody
    public boolean addProduct(@RequestBody Product newProduct) throws ServiceException {
        return productService.add(newProduct);
    }
}
