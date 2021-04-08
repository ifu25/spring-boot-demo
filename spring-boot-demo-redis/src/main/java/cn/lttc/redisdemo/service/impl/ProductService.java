package cn.lttc.redisdemo.service.impl;

import cn.lttc.redisdemo.mapper.ProductMapper;
import cn.lttc.redisdemo.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: sunj
 * @time: 2021/04/07 18:17
 */
@Service
public class ProductService implements cn.lttc.redisdemo.service.ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public Product findProductById(Integer id) {
        Product product = null;
        String key = "product:"+id;
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
}
