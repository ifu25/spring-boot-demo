package cn.lttc.redisdemo.service.impl;

import cn.lttc.redisdemo.dto.CartDto;
import cn.lttc.redisdemo.dto.ProductDto;
import cn.lttc.redisdemo.mapper.ProductMapper;
import cn.lttc.redisdemo.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 产品模块Service层
 *
 * @author sunjian
 * @create 2021-04-16
 */
@Service
public class ProductServiceImpl implements cn.lttc.redisdemo.service.ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     *  根据产品id获取产品
     * @param id 产品id
     * @return cn.lttc.redisdemo.model.Product 产品实体
     */
    @Override
    public Product findProductById(Integer id) {
        Product product = null;
        String key = "product:"+id;
        //若redis中包含此key，则从redis中获取，若不包含，则从mysql数据库中获取
        if(redisTemplate.hasKey(key)){
            System.out.println("从redis中获取");
            redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Product>(Product.class));
            product =(Product) redisTemplate.opsForValue().get(key);
        }else {
            System.out.println("从数据库中获取");
            product= productMapper.findProductById(id);
            redisTemplate.opsForValue().set(key,product);
        }
        return product;
    }

    /**
     * 获取所有产品，并将产品添加到redis中
     * @return List<Product> 产品集合
     */
    @Override
    public List<Product> findAllProduct() {
        List<Product> allProduct = productMapper.findAllProduct();
        for (Product product : allProduct) {
            redisTemplate.opsForHash().put("product",product.getId(),product);
        }
        return allProduct;
    }

    /**
     * 以"user:用户id"为key，产品id为hashkey，去redis中查询，
     *   若不存在此key，将对应信息添加到redis中
     *   若存在，此key对应的值+count，即：新值=原值+count
     * @param userId 用户id
     * @param count 数量
     * @param productId 产品id
     * @return void
     */
    @Override
    public void addProductToCartToRedis(Integer userId, Integer productId, Integer count) {
        if(redisTemplate.opsForHash().get("user:"+userId, productId)==null){
            redisTemplate.opsForHash().put("user:"+userId, productId,count);
        }else {
            redisTemplate.opsForHash().increment("user:"+userId, productId,count);
        }
    }

    /**
     * 获取购物车信息
     * 1.使用redisTemplate根据用户id，获取用户购物车中所有产品key信息；
     * 2.遍历产品key信息，获取对应产品和对应产品的数量
     * 3.组装cartDto返回给前端
     * @param userId 用户id
     * @return cn.lttc.redisdemo.dto.CartDto
     */
    @Override
    public CartDto getCartDaoByUserId(Integer userId) {
        CartDto cartDto = new CartDto();
        List<ProductDto> productDtoList = new ArrayList<>();
        Set<Object> productKeys = redisTemplate.opsForHash().keys("user:" + userId);
        for (Object productKey : productKeys) {
            ProductDto productDto = new ProductDto();
            redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Integer>(Integer.class));
            Integer count = (Integer)redisTemplate.opsForHash().get("user:" + userId, productKey);
            redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Product>(Product.class));
            Product product = (Product)redisTemplate.opsForHash().get("product", (Integer)productKey);
            productDto.setCount(count);
            productDto.setProduct(product);
            productDtoList.add(productDto);
        }
        cartDto.setUserId(userId);
        cartDto.setProductDto(productDtoList);
        return cartDto;
    }

    /**
     * 使用"user:userId"为key，productId为hashkey，去redis中找到对应值，并对其进行+1操作
     * @param userId 用户id
     * @param productId 产品id
     * @return void
     */
    @Override
    public void productIncr(Integer userId, Integer productId) {
        redisTemplate.opsForHash().increment("user:"+userId,productId,1);
    }

    /**
     * 使用"user:userId"为key，productId为hashkey，去redis中找到对应值，并对其进行-1操作
     * @param userId 用户id
     * @param productId 产品id
     * @return void
     */
    @Override
    public void productDecr(Integer userId, Integer productId) {
        redisTemplate.opsForHash().increment("user:"+userId,productId,-1);
    }
}
