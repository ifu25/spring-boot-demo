package cn.lttc.redisdemo.web;


import cn.lttc.redisdemo.model.Product;
import cn.lttc.redisdemo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @description:
 * @author: sunj
 * @time: 2021/04/07 18:19
 */
@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    @RequestMapping("/findProductById/{id}")
    public ModelAndView findProductById(@PathVariable("id") Integer id,ModelAndView modelAndView){
        Product product = productService.findProductById(id);
        modelAndView.setViewName("product");
        modelAndView.addObject("product",product);
        return modelAndView;
    }
}
