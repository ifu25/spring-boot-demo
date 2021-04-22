package cn.lttc.redisdemo.mapper;

import cn.lttc.redisdemo.model.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProductMapper {
    // 根据产品id获取产品
    Product findProductById(Integer id);

    //获取所有产品
    List<Product> findAllProduct();
}
