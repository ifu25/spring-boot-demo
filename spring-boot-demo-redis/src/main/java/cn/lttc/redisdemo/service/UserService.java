package cn.lttc.redisdemo.service;

import cn.lttc.redisdemo.model.User;

import java.util.List;

/**
 * 用户模块Service接口
 *
 * @author sunjian
 * @create 2021-04-16
 */
public interface UserService {
    /**
     * 获取所有用户
     * @param
     * @return java.util.List<cn.lttc.redisdemo.model.User> 用户集合
     */
    List<User> findAllUser();
}
