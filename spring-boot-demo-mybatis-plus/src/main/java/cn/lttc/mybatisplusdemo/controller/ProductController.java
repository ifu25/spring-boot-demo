package cn.lttc.mybatisplusdemo.controller;


import cn.lttc.mybatisplusdemo.entity.Product;
import cn.lttc.mybatisplusdemo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * product控制器
 *
 * @author YangShuangLing
 * @create 2021-04-19
 */
@RestController
@RequestMapping("/mybatisplusdemo/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 测试mybatisplus是否有一级缓存
     * 说明：通过请求两次id相同的product测试mybatisplus有缓存机制
     * 结论：两次get均初始化sqlsession，执行sql语句
     * @return
     */
    @RequestMapping("/get")
    public Product getProductById(){
        Product product1 = productService.getById("1380336834531348481L");
        Product product2 = productService.getById("1380336834531348481L");
        return product1;
    }



}
