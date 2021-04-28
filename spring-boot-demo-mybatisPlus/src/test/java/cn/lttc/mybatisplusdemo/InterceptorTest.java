package cn.lttc.mybatisplusdemo;

import cn.lttc.mybatisplusdemo.entity.Demo;
import cn.lttc.mybatisplusdemo.entity.Product;
import cn.lttc.mybatisplusdemo.service.DemoService;
import cn.lttc.mybatisplusdemo.service.ProductService;
import cn.lttc.mybatisplusdemo.util.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 插件测试类
 *
 * @author YangShuangLing
 * @create 2021-04-19
 */
@SpringBootTest
public class InterceptorTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private DemoService demoService;

    //region ========== 分页 ==========

    // 调用 BaseMapper 的 selectPage 方法，分页查询
    @Test
    public void testPage(){
        Page<Product> page = new Page<>(1, 3);
        productService.page(page, new QueryWrapper<Product>());
        PageUtils pageUtils = new PageUtils(page);
        System.out.println(pageUtils);
    }

    //endregion

    //region ========== 全表删除 ==========
    @Test
    public void testDelete(){
        productService.remove(null);
    }
    //endregion

    //region ========== 乐观锁 ==========

    //添加数据（演示自动填充version）
    @Test
    public void addData(){

        Demo demo = new Demo();
        demo.setAge(12);
        demo.setName("张三");
        demo.setEmail("122@qq.com");
        demoService.save(demo);

    }

    //更新数据（先查后改，才可以）
    @Test
    public void updateData1(){

        //模拟并发，demo1成功，demo2失败
        Demo demo1 = demoService.getById(1385027232805748738L);
        Demo demo2 = demoService.getById(1385027232805748738L);
        demo1.setAge(70);
        demo2.setAge(22);
        demoService.updateById(demo1);
        demoService.updateById(demo2);

    }

    //更新数据（没有查询直接修改，version不起作用）
    @Test
    public void updateData2(){

        Demo demo = new Demo();
        demo.setId(1384818588969369602L);
        demo.setName("李四");
        demo.setAge(23);
        demoService.updateById(demo);

    }

    //endregion
}
