package cn.lttc.redisdemo.mapper;

import cn.lttc.redisdemo.model.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper {
    Product findProductById(Integer id);
}
