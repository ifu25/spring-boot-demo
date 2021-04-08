package cn.lttc.redisdemo.service;

import cn.lttc.redisdemo.model.Product;

public interface ProductService {
    Product findProductById(Integer id);
}
