package cn.lttc.swaggerdemo.web

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 首页控制器
 *
 * @author：邢  港
 * @create：2021-03-20
 */

@RestController
@Api(tags = ["首页"])
class HomeController {

    @GetMapping("/")
    @ApiOperation("获得指定用户编号的用户", notes = "目前仅作为测试，所以返回一个字符串")
    fun index(): Any {
        return "Hello Swagger + knife4j！<a href= '/doc.html'>点此浏览文档>></a>"
    }
}