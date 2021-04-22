package cn.lttc.mybatisplusdemo.service;

import cn.lttc.mybatisplusdemo.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;

/**
 * Product业务逻辑接口
 *
 * @author YangShuangLing
 * @create 2021-04-19
 */
public interface ProductService extends IService<Product> {

    /**
     * 根据名称批量删除
     * @param list
     */
    void deleteByNames(ArrayList<String> list);
}
