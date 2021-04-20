package cn.lttc.hellodemo.web

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 首页控制器
 *
 * @author xinggang
 * @create 2021-03-20
 * @modify 2021-04-19，xinggang，修改文档注释格式
 */
@RestController
class HomeController {

    @RequestMapping("/")
    fun index(): Any {
        return "Hello Spring Boot Demo with Java/Kotlin/Gradle."
    }

    /**
     * 方法注释演示
     *
     * @param name 姓名
     * @return 用户信息JSON字符串
     */
    @RequestMapping("methodCommentDemo")
    fun methodCommentDemo(name: String): String {

        //这里是行注释，对一些有必要进行解释的语句进行说明
        val result = name.replace("小", "大")

        //返回结果
        return "hello:$result"
    }
}