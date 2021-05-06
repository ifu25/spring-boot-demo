package cn.lttc.mybatisplusdemo;

import cn.lttc.mybatisplusdemo.mapper.UserMapper;
import cn.lttc.mybatisplusdemo.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 快速启动测试类
 *
 * @author YangShuangLing
 * @create 2021-04-19
 */
@SpringBootTest
class HelloDemoTests {

    @Resource
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- fast start ------"));
        List<User> userList = userMapper.selectList(null);
        System.out.println(userList);
    }
}
