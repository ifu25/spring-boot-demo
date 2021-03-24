package cn.lttc.shirodemo.web

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.IncorrectCredentialsException
import org.apache.shiro.authc.UnknownAccountException
import org.apache.shiro.authc.UsernamePasswordToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/****************************************
 * 用户控制器
 * 作者：XingGang
 * 日期：2021-03-24
 * 网址：https://weiku.co
 * 邮箱：xinggang.china@gmail.com
 ****************************************/
@RestController
class UserController {

    @PostMapping("/login")
    fun login(username: String, password: String): Any {
        val subject = SecurityUtils.getSubject()
        val token = UsernamePasswordToken(username, password)
        return try {
            subject.login(token)
            "登录成功，<a href='/'>返回主页</a>"
        } catch (ex: UnknownAccountException) {
            ex.printStackTrace()
            "用户名不存在"
        } catch (ex: IncorrectCredentialsException) {
            ex.printStackTrace()
            "密码错误"
        }
    }

    @RequestMapping("/logout")
    fun logout(): Any {
        val subject = SecurityUtils.getSubject()
        subject.logout()
        return "已退出，<a href='/'>返回主页</a>"
    }
}