package cn.lttc.redisdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @description:
 * @author: sunj
 * @time: 2021/04/01 18:54
 */
@SpringBootApplication
@MapperScan("cn.lttc.redisdemo.mapper")
public class RedisDemo {
    public static void main(String[] args) {
        SpringApplication.run(RedisDemo.class,args);
    }
}
