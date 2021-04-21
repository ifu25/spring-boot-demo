package cn.lttc.redisdemo.service.impl;

import cn.lttc.redisdemo.mapper.UserMapper;
import cn.lttc.redisdemo.model.User;
import cn.lttc.redisdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户模块Service层
 *
 * @author sunjian
 * @create 2021-04-16
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    public UserMapper userMapper;

    /**
     * @return java.util.List<cn.lttc.redisdemo.model.User> 用户集合
     */
    @Override
    public List<User> findAllUser() {
        return userMapper.findAllUser();
    }
}
