package cn.lttc.mybatisplusdemo;

import cn.lttc.mybatisplusdemo.entity.Product;
import cn.lttc.mybatisplusdemo.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 增删改查测试类
 *
 * @author YangShuangLing
 * @create 2021-04-19
 */
@SpringBootTest
public class BaseCrudTests {

    @Autowired
    private ProductService productService;

    //region ========== 增 ==========

    //保存
    @Test
    public void testSave() {
        Product product = new Product();
        product.setName("耳机");
        product.setPrice(new BigDecimal(350));
        productService.save(product);
    }

    //批量保存
    @Test
    public void testSaveBetch(){
        ArrayList<Product> products = new ArrayList<>();

        Product product1 = new Product();
        product1.setName("电脑");
        product1.setPrice(new BigDecimal(7000));
        Product product2 = new Product();
        product2.setName("电视机");
        product2.setPrice(new BigDecimal(5000));

        products.add(product1);
        products.add(product2);

        productService.saveBatch(products);
    }

    //endregion

    //region ========== 增或改 ==========

    //id 若存在，则修改，id 不存在则新增数据
    @Test
    public void testSaveOrUpdate(){
        //先 SELECT proid,name,sub_title,main_image,price,stock,status,create_time,update_time FROM t_product WHERE proid=? AND status=1
        //如存在，则 UPDATE t_product SET name=?, price=?, update_time=? WHERE proid=? AND status=1
        Product product = new Product();
        product.setProid(1380321956081004546L);
        product.setName("桌子");
        product.setPrice(new BigDecimal(10));
        productService.saveOrUpdate(product);
    }

    // 根据updateWrapper尝试更新，否继续执行saveOrUpdate(T)方法
    @Test
    public void testSaveOrUpdateW(){
        /**
         * 1. UPDATE t_product SET name=?, update_time=? WHERE status=1 AND (price = ?)
         * 2. 1）如果1有记录被修改，结束
         *    2）如果1没有记录没修改，则摒弃UpdateWrapper中的条件：
         *              SELECT proid,name,sub_title,main_image,price,stock,status,create_time,update_time FROM t_product WHERE proid=? AND status=1
         *              UPDATE t_product SET name=?, update_time=? WHERE proid=? AND status=1
         */
        Product product = new Product();
        product.setProid(1380331496314810370L);
        product.setName("耳机");
        productService.saveOrUpdate(product,new UpdateWrapper<Product>().eq("price",3770));
    }

    // 批量插入或修改数据
    @Test
    public void testSaveOrUpdateBatch(){
        //指明id的更新，没有指明id的插入
        ArrayList<Product> list = new ArrayList<>();
        Product product1 = new Product();
        product1.setProid(1384779667703173122L);
        product1.setPrice(new BigDecimal(20));
        Product product2 = new Product();
        product2.setName("桌子");
        product2.setPrice(new BigDecimal(300));
        list.add(product1);
        list.add(product2);
        productService.saveOrUpdateBatch(list);
    }

    //endregion

    //region ========== 删 ==========

    //调用 BaseMapper 的 deleteById 方法，根据 id 删除数据
    @Test
    public void testRemoveById() {
        productService.removeById(1379976405007228929L);
    }

    //调用 BaseMapper 的 deleteByMap 方法，根据 map 定义字段的条件删除
    @Test
    public void testRemoveByMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("name","电脑");
        map.put("price",7000);
        productService.removeByMap(map);
    }

    //调用 BaseMapper 的 delete 方法，根据实体类定义的 条件删除对象
    //删除name包含‘手’的且价格大于5000的商品
    @Test
    public void testRemove(){
        //UPDATE t_product SET status=0 WHERE status=1 AND (name LIKE ? AND price = ?)
        productService.remove(new QueryWrapper<Product>().like("name","手").eq("price",9000));
    }

    //调用 BaseMapper 的 deleteBatchIds 方法, 进行批量删除。
    @Test
    public void testRemoveByIds(){
        //UPDATE t_product SET status=0 WHERE proid IN ( ? , ? ) AND status=1
        ArrayList<Long> list = new ArrayList<>();
        list.add(1379976964686774273L);
        list.add(1380071882008588290L);
        productService.removeByIds(list);
    }

    //根据名称批量删除
    @Test
    public void testDeleteByNames(){
        //DELETE from t_product where name=? OR name=?
        ArrayList<String> list = new ArrayList<>();
        list.add("电视机");
        list.add("冰箱");
        productService.deleteByNames(list);
    }

    //endregion

    //region ========== 改 ==========

    //调用 BaseMapper 的 updateById 方法，根据 ID 选择修改
    @Test
    public void testUpdateById(){
        Product product = new Product();
        product.setProid(1380089294963712002L);
        product.setSubTitle("年终大促！！！");
        productService.updateById(product);
    }

    // 调用 BaseMapper 的 update 方法，修改满足 updateWrapper 条件的实体对象
    @Test
    public void testUpdate(){
        Product product = new Product();
        product.setName("手机壳");
        productService.update(product ,new UpdateWrapper<Product>().eq("name","桌子"));
    }

    // 根据id批量更新数据
    @Test
    public void testUpdateBatchById(){
        ArrayList<Product> list = new ArrayList<>();
        Product product1 = new Product();
        product1.setProid(1380336142253109250L);
        product1.setStock(200);
        Product product2 = new Product();
        product2.setProid(1380336834531348481L);
        product2.setSubTitle("便宜处理哈！！！");
        list.add(product1);
        list.add(product2);
        productService.updateBatchById(list);
    }
    //endregion

    //region ========== 查询一条 ==========

    // 调用 BaseMapper 的 selectById 方法，根据 主键 ID 返回数据。
    @Test
    public void testGetById(){
        Product product = productService.getById(1380336834531348481L);
        System.out.println(product);
    }

    // 查询一条记录,如果返回多条报错
    @Test
    public void testGetOne(){
        Product product = productService.getOne(new QueryWrapper<Product>().between("price", 10, 300).eq("name","手机壳"));
        System.out.println(product);
    }

    // 返回一条记录（map 保存）
    @Test
    public void testGetMap(){
        Map<String, Object> map = productService.getMap(new QueryWrapper<Product>().eq("proid", 1379976405007228929L));
        System.out.println(map);
    }

    //endregion

    //region ========== 查询多条 ==========

    // 调用 BaseMapper 的 selectBatchIds 方法，批量查询数据。
    @Test
    public void testListByIds(){
        ArrayList<Long> list = new ArrayList<>();
        list.add(1379976405007228929L);
        list.add(1380089294963712002L);
        productService.listByIds(list).forEach(System.out::println);
    }

    // 调用 BaseMapper 的 selectByMap 方法，根据表字段条件查询
    @Test
    public void testListByMap(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("name","耳机");
        map.put("sub_title","哈哈哈");
        productService.listByMap(map).forEach(System.out::println);
    }

    // 返回所有数据
    @Test
    public void testList(){
        productService.list().forEach(System.out::println);
    }

    // 调用 BaseMapper 的 selectList 方法，查询所有记录（返回 entity 集合）。
    @Test
    public void testListW(){
        productService.list(new QueryWrapper<Product>().eq("name","电脑")).forEach(System.out::println);
    }

    // 调用 BaseMapper 的 selectMaps 方法，查询所有记录（返回 map 集合）
    @Test
    public void testListMaps(){
        productService.listMaps(new QueryWrapper<Product>().between("price", 100, 10000)).forEach(System.out::println);
    }

    // 返回全部记录，但只返回第一个字段的值
    @Test
    public void testListObjs(){
        productService.listObjs().forEach(System.out::println);
    }

    //endregion

    //region ========== 查询记录数 ==========

    // 查询总记录数
    @Test
    public void testCount(){
        int count = productService.count();
        System.out.println(count);
    }

    // 根据 Wrapper 条件，查询总记录数
    @Test
    public void testCountW(){
        int count = productService.count(new QueryWrapper<Product>().between("price", 100, 10000));
        System.out.println(count);
    }

    //endregion

    //region ========== 链式查询 ==========

    @Test
    public void testChain(){
        productService.query().eq("name", "手机壳").list();
    }

    //endregion

    //region ========== AR模式的CRUD ==========

    //增
    @Test
    public void testInsert(){
        Product product = new Product();
        product.setName("AR测试");
        product.setPrice(new BigDecimal(100));
        product.insert();
    }

    //查
    @Test
    public void testSelectById(){
        Product product = new Product();
        Integer count = product.selectCount(new QueryWrapper<Product>().eq("proid", 1380693802089496577L));
        System.out.println(count);
    }

    //改
    @Test
    public void testUpdataById(){
        Product product = new Product();
        product.setProid(1380693802089496577L);
        product.setName("修改AR");
        boolean b = product.updateById();
        System.out.println(b);
    }

    //新增或更新(如果存在ID就更新，没有就增加)
    @Test
    public void testInsertOrUpdate(){
        Product product = new Product();
        product.setProid(1380693802089496577L);
        product.setSubTitle("条条大路通罗马");
        boolean b = product.insertOrUpdate();
        System.out.println(b);
    }

    //删
    @Test
    public void test(){
        Product product = new Product();
        boolean b = product.deleteById(1379976964686774273L);
        System.out.println(b);
    }
    //............

    //endregion

}
