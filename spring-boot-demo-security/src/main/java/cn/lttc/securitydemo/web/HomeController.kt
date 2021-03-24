package cn.lttc.securitydemo.web

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/****************************************
 * 首页控制器
 * 作者：XingGang
 * 日期：2021-03-24
 * 网址：https://weiku.co
 * 邮箱：xinggang.china@gmail.com
 ****************************************/
@RestController
open class HomeController {

    @RequestMapping("/")
    fun index(): Any {

        return "hello spring security<br><br><a href='/admin'>/admin</a> | <a href='/test'>/test</a>"
    }

    @RequestMapping("/admin")
    fun admin(): Any {
        return "<a href='/'><<返回主页</a> <br><br><h1>hello admin</h1>"
    }

    @RequestMapping("/test")
    fun test(): Any {
        return "<a href='/'><<返回主页</a> <br><br><h1>hello test</h1>"
    }

    @RequestMapping("/admin2")
    @PreAuthorize("hasRole('test')")
    open fun admin2(): Any {
        return "<a href='/'><<返回主页</a> <br><br><h1>hello admin2</h1>"
    }
}