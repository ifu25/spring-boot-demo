package cn.lttc.redisdemo.mapper;

import cn.lttc.redisdemo.model.User;

import java.util.List;
public interface UserMapper {
    //获取所有用户
    List<User> findAllUser();
}
