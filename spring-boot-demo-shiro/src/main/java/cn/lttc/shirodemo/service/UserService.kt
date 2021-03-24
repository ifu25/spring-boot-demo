package cn.lttc.shirodemo.service

import cn.lttc.shirodemo.entity.UserEntity
import cn.lttc.shirodemo.mapper.UserMapper
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/****************************************
 * 用户服务类
 * 作者：XingGang
 * 日期：2021-03-20
 * 网址：https://weiku.co
 * 邮箱：xinggang.china@gmail.com
 ****************************************/
@Service
class UserService {

    @Autowired
    lateinit var userMapper: UserMapper

    /**
     * 根据用户名查询用户
     */
    fun findByUsername(username: String): UserEntity? {
        val query = QueryWrapper<UserEntity>()
        query.eq("username", username)
        return userMapper.selectOne(query)
    }
}