package cn.lttc.mybatisplusdemo.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 配置自动填充策略
 *
 * @author YangShuangLing
 * @create 2021-04-19
 */
@Component
public class FillObjectHandler implements MetaObjectHandler {

    //插入时填充
    @Override
    public void insertFill(MetaObject metaObject) {

        //填充时间
        this.setFieldValByName("createTime",new Date(),metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);

        //填充版本
        this.setFieldValByName("version",1,metaObject);

    }

    //更新时填充
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}
