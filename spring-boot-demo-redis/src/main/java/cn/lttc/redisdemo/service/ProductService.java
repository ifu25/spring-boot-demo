package cn.lttc.redisdemo.service;

import cn.lttc.redisdemo.dto.CartDto;
import cn.lttc.redisdemo.model.Product;
import java.util.List;

/**
 * 产品模块Service接口
 *
 * @author sunjian
 * @create 2021-04-16
 */

public interface ProductService {

    /**
     * 根据产品id获取产品
     * @param id 产品id
     * @return cn.lttc.redisdemo.model.Product 产品
     */
    Product findProductById(Integer id);
    /**
     * 获取所有产品
     * @return java.util.List<cn.lttc.redisdemo.model.Product> 产品集合
     */
    List<Product> findAllProduct();
    /**
     * 添加产品到购物车
     * @param userId 用户id
     * @param productId 产品id
     * @param count 产品数量
     * @return void
     */
    void addProductToCartToRedis(Integer userId,Integer productId,Integer count);
    /**
     * 根据用户id获取购物车信息
     * @param userId 用户id
     * @return cn.lttc.redisdemo.dto.CartDto
     */
    CartDto getCartDaoByUserId(Integer userId);
    /**
     * 购物车中产品数量+1
     * @param userId 用户id
     * @param productId 产品id
     * @return void
     */
    void productIncr(Integer userId, Integer productId);
    /**
     * 购物车中产品数量-1
     * @param userId 用户id
     * @param productId 产品id
     * @return void
     */
    void productDecr(Integer userId, Integer productId);
}
