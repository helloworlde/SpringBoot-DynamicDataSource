package cn.com.hellowood.dynamicdatasource.controller;

import cn.com.hellowood.dynamicdatasource.common.CommonResponse;
import cn.com.hellowood.dynamicdatasource.common.ResponseUtil;
import cn.com.hellowood.dynamicdatasource.modal.Product;
import cn.com.hellowood.dynamicdatasource.service.ProductService;
import cn.com.hellowood.dynamicdatasource.utils.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private ProductService productService;

    /**
     * Get product by id
     *
     * @param productId
     * @return
     * @throws ServiceException
     */
    @GetMapping("/{id}")
    public CommonResponse getProduct(@PathVariable("id") Long productId) throws ServiceException {
        return ResponseUtil.generateResponse(productService.select(productId));
    }

    /**
     * Get all product
     *
     * @return
     * @throws ServiceException
     */
    @GetMapping
    public CommonResponse getAllProduct() {
        return ResponseUtil.generateResponse(productService.getAllProduct());
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
    public CommonResponse updateProduct(@PathVariable("id") Long productId, @RequestBody Product newProduct) throws ServiceException {
        return ResponseUtil.generateResponse(productService.update(productId, newProduct));
    }

    /**
     * Delete product by id
     *
     * @param productId
     * @return
     * @throws ServiceException
     */
    @DeleteMapping("/{id}")
    public CommonResponse deleteProduct(@PathVariable("id") long productId) throws ServiceException {
        return ResponseUtil.generateResponse(productService.delete(productId));
    }

    /**
     * Save product
     *
     * @param newProduct
     * @return
     * @throws ServiceException
     */
    @PostMapping
    public CommonResponse addProduct(@RequestBody Product newProduct) throws ServiceException {
        return ResponseUtil.generateResponse(productService.add(newProduct));
    }
}
