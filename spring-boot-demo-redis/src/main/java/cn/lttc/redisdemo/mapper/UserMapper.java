package cn.lttc.redisdemo.mapper;

import cn.lttc.redisdemo.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserMapper {
    //获取所有用户
    List<User> findAllUser();
}
