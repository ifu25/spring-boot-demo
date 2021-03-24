package cn.lttc.shirodemo.config

import cn.lttc.shirodemo.entity.UserEntity
import cn.lttc.shirodemo.service.UserService
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.SimpleAuthenticationInfo
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.authz.SimpleAuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

/****************************************
 * 自定义 Realm 实现（完成认证和授权）
 * 作者：XingGang
 * 日期：2021-03-20
 * 网址：https://weiku.co
 * 邮箱：xinggang.china@gmail.com
 ****************************************/
class ShiroRealm : AuthorizingRealm() {

    @Autowired
    lateinit var userService: UserService

    /**
     * 认证
     */
    override fun doGetAuthenticationInfo(token: AuthenticationToken?): AuthenticationInfo? {
        token as UsernamePasswordToken
        //从数据库获取用户信息
        val user = userService.findByUsername(token.username)
        if (user != null) {
            return SimpleAuthenticationInfo(user, user.password, user.username)
        }

        return null
    }

    /**
     * 授权
     */
    override fun doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo {
        //获取当前登录的用户信息
        val subject = SecurityUtils.getSubject()
        val user = subject.principal as UserEntity

        //也可以这样获取当前用户
        //val user = principals.primaryPrincipal as UserEntity

        //使用 SimpleAuthenticationInfo 保存授权信息
        val authorInfo = SimpleAuthorizationInfo()

        //设置角色（这里简单演示数据库中只保存了一个角色名称，实际多个时需要循环添加到集合）
        user.role?.let {
            val roles = HashSet<String>()
            roles.add(it)

            authorInfo.roles = roles
            println("添加角色：${it}")
        }

        //设置权限（这里简单演示数据库中只保存了一个权限名称，实际多个时需要循环添加到集合）
        user.perms?.let {
            authorInfo.addStringPermission(it)
            println("添加权限：${it}")
        }

        return authorInfo
    }
}