package cn.lttc.hellodemo.web

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/****************************************
 * 首页控制器
 * 作者：XingGang
 * 日期：2021-03-20
 * 网址：https://weiku.co
 * 邮箱：xinggang.china@gmail.com
 ****************************************/
@RestController
class HomeController {

    @RequestMapping("/")
    fun index(): Any {
        return "Hello Spring Boot Demo with Java/Kotlin/Gradle."
    }
}