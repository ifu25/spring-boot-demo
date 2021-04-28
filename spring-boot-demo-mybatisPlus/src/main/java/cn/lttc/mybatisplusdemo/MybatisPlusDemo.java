package cn.lttc.mybatisplusdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类
 *
 * @author YangShuangLing
 * @create 2021-04-19
 */
@SpringBootApplication
@MapperScan("cn.lttc.mybatisplusdemo.mapper")
public class MybatisPlusDemo {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusDemo.class, args);
    }

}
