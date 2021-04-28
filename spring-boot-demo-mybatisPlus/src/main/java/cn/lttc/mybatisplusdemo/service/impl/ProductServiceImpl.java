package cn.lttc.mybatisplusdemo.service.impl;

import cn.lttc.mybatisplusdemo.entity.Product;
import cn.lttc.mybatisplusdemo.mapper.ProductMapper;
import cn.lttc.mybatisplusdemo.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Product业务逻辑实现类
 *
 * @author YangShuangLing
 * @create 2021-04-19
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public void deleteByNames(ArrayList<String> list) {
        baseMapper.deleteByNames(list);
    }
}
