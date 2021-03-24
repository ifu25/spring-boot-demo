package cn.lttc.shirodemo.entity

import com.baomidou.mybatisplus.annotation.TableName

/****************************************
 * 用户实体
 * 作者：XingGang
 * 日期：2021-03-20
 * 网址：https://weiku.co
 * 邮箱：xinggang.china@gmail.com
 ****************************************/
@TableName("sys_user")
class UserEntity {
    var id: Int? = null
    var username: String? = null
    var password: String? = null
    var perms: String? = null
    var role: String? = null
}