package cn.lttc.redisdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @description:
 * @author: sunj
 * @time: 2021/04/01 19:09
 */
@Configuration
public class MyRedisTemplate {
    @Bean
    public RedisTemplate<String,Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        //自定义实现对于RedisTemplate的配置
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        //设置一JSON格式存储或者获取对象
        template.setKeySerializer(new StringRedisSerializer());
        Jackson2JsonRedisSerializer<Object> ser = new Jackson2JsonRedisSerializer<Object>(Object.class);
        //为templeate设置默认的序列化方式
        template.setDefaultSerializer(ser);
        return template;
    }
}
