package cn.lttc.mybatisplusdemo.mapper;

import cn.lttc.mybatisplusdemo.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

/**
 * ProductMapper接口
 *
 * @author YangShuangLing
 * @create 2021-4-19
 */
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 根据名称批量删除
     * @param list
     */
    void deleteByNames(@Param("names") ArrayList<String> list);

}
