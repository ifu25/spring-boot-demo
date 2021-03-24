package cn.lttc.shirodemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "cn.lttc.shirodemo.mapper")
public class ShiroDemo {

    public static void main(String[] args) {
        SpringApplication.run(ShiroDemo.class, args);
    }

}
