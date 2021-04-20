package cn.lttc.thymeleafdemo.model

/**
 * 用户实体类
 *
 * @author xinggang
 * @create 2021-04-20
 */
data class UserEntity(
    var userName: String? = null,
    var age: Int? = null,
    var email: String? = null,
    var deptName: String? = null,
    var mobile: String? = null
)
