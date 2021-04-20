package cn.lttc.redisdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * redisDemo启动器
 *
 * @author sunjian
 * @create 2021-04-16
 */
@SpringBootApplication
@MapperScan("cn.lttc.redisdemo.mapper")
public class RedisDemo {
    public static void main(String[] args) {
        SpringApplication.run(RedisDemo.class,args);
    }
}
