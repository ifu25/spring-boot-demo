package cn.lttc.logbackdemo.web

import org.slf4j.LoggerFactory
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

    var logger = LoggerFactory.getLogger(HomeController::class.java)

    @RequestMapping("/")
    fun index(): Any {
        logger.debug("这是 debug 日志")
        logger.info("这是 info 日志")
        logger.warn("这是 warn 日志")
        logger.error("这是 error 日志")

        return "日志演示，请查看控制台输出。"
    }
}