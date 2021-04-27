package cn.lttc.exceptiondemo.web

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.IOException

/**
 * 首页控制器
 *
 * @author xinggang
 * @create 2021-04-27
 */
@RestController
class HomeController {

    /**
     * 运行时异常
     */
    @RequestMapping("/1")
    fun demo1(): Any {
        throw RuntimeException("运行时异常")
        return "欢迎进入管理后台"
    }

    /**
     * 空对象引用异常，id 为非空类型，不提供会异常
     */
    @RequestMapping("/2")
    fun demo2(id: String): Any {
        return "您要查询的id：${id}"
    }

    /**
     * 参数校验异常
     */
    @RequestMapping("/3")
    fun demo3(@RequestParam(required = true) id: String): Any {
        return "您要查询的id：${id}"
    }

    /**
     * 其它异常：模拟 IO 异常
     */
    @RequestMapping("/4")
    fun demo4(): Any {
        throw IOException("文件不存在异常")
        return "演示其它异常"
    }
}