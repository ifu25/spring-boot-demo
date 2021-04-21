package cn.lttc.redisdemo.web;


import cn.lttc.redisdemo.dto.CartDto;
import cn.lttc.redisdemo.dto.IncrDto;
import cn.lttc.redisdemo.dto.ProductDto;
import cn.lttc.redisdemo.model.Product;
import cn.lttc.redisdemo.service.ProductService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;


/**
 * 产品Controller层
 *
 * @author sunjian
 * @create 2021-04-16
 */
@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    //region ===========================购物车demo===========================

    /**
     * 根据产品id获取产品
     *
     * @param id 产品id
     * @return org.springframework.web.servlet.ModelAndView
     */
    @RequestMapping("/findProductById/{id}")
    public ModelAndView findProductById(@PathVariable("id") Integer id, ModelAndView modelAndView) {
        Product product = productService.findProductById(id);
        modelAndView.setViewName("product");
        modelAndView.addObject("product", product);
        return modelAndView;
    }

    /**
     * 获取全部产品
     *
     * @param modelAndView mv
     * @return org.springframework.web.servlet.ModelAndView
     */
    @RequestMapping("/findAllProduct")
    public ModelAndView findAllProduct(ModelAndView modelAndView) {
        List<Product> productLst = productService.findAllProduct();
        modelAndView.setViewName("product");
        modelAndView.addObject("productLst", productLst);
        return modelAndView;
    }

    /**
     * 添加产品到购物车
     *
     * @param cartDto 购物车Dto
     * @return com.alibaba.fastjson.JSONObject
     */
    @RequestMapping("/cart/addProduct")
    @ResponseBody
    public JSONObject addProductToCart(@RequestBody CartDto cartDto) {
        JSONObject retJson = new JSONObject();
        try {
            Integer userId = cartDto.getUserId();
            List<ProductDto> productDtoLst = cartDto.getProductDto();
            for (ProductDto productDto : productDtoLst) {
                Integer productId = productDto.getProduct().getId();
                Integer count = productDto.getCount();
                productService.addProductToCartToRedis(userId, productId, count);
            }

        } catch (Exception e) {
            retJson.put("msg", "添加购物车失败");
        }
        retJson.put("msg", "添加购物车成功");
        return retJson;
    }

    /**
     * 购物车主界面
     *
     * @param modelAndView mv
     * @param userId       用户id
     * @return org.springframework.web.servlet.ModelAndView
     */
    @RequestMapping("/cart/main/{userId}")
    public ModelAndView cartMain(ModelAndView modelAndView, @PathVariable Integer userId) {
        CartDto cartDto = productService.getCartDaoByUserId(userId);
        modelAndView.addObject("cartDto", cartDto);
        modelAndView.setViewName("cart-index");
        return modelAndView;
    }

    /**
     * 购物车产品+1
     *
     * @param incrDto 购物车增加产品数量Dto
     * @return com.alibaba.fastjson.JSONObject
     */
    @RequestMapping("/cart/productIncr")
    @ResponseBody
    public JSONObject producyIncr(@RequestBody IncrDto incrDto) {
        JSONObject retJson = new JSONObject();
        try {
            Integer userId = incrDto.getUserId();
            Integer productId = incrDto.getProductId();
            productService.productIncr(userId, productId);
            retJson.put("msg", "success");
        } catch (Exception e) {
            retJson.put("msg", e.getMessage());
        }
        return retJson;
    }

    /**
     * 购物车产品-1
     *
     * @param incrDto 购物车减少产品数量Dto
     * @return com.alibaba.fastjson.JSONObject
     */
    @RequestMapping("/cart/productDecr")
    @ResponseBody
    public JSONObject producyDecr(@RequestBody IncrDto incrDto) {
        JSONObject retJson = new JSONObject();
        try {
            Integer userId = incrDto.getUserId();
            Integer productId = incrDto.getProductId();
            productService.productDecr(userId, productId);
            retJson.put("msg", "success");
        } catch (Exception e) {
            retJson.put("msg", e.getMessage());
        }
        return retJson;
    }

    //endregion
}
