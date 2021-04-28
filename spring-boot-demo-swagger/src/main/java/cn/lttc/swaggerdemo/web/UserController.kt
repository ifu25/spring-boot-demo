package cn.lttc.swaggerdemo.web

import cn.lttc.swaggerdemo.entity.UserEntity
import io.swagger.annotations.*
import org.springframework.web.bind.annotation.*
import springfox.documentation.annotations.ApiIgnore
import java.util.*


/**
 * 用户控制器
 *
 * @author：谢  鑫
 * @create：2021-04-17
 */

@RestController
@Api(tags = ["用户接口"])
class UserController {

    /**
     * 演示 @ApiOperation注解
     */
    @ApiOperation(
        value = "获取用户列表",
        notes = "目前仅仅是测试demo，返回用户全列表",
        tags = ["用户接口"], //和 @Api 注解的 tags 属性一致。若二者不一致，则此接口会出现在其 tags 属性对应的标签下。
        nickname = "a" //若同一个 Controller 不同API的 nickname 属性配置相同，则 API 操作接口标识会自动添加数字下标
    )
    @GetMapping("/getList")
    fun getList(): Any {
        // 查询列表
        val result = ArrayList<UserEntity>()

        //测试用户1
        val user1 = UserEntity()
        user1.id = 1
        user1.userName = "张三"
        user1.sex = "男"
        user1.age = 20
        user1.address = "状元府小区1号楼1单元101"
        user1.phoneNumber = "18656231478"
        result.add(user1)
        //测试用户2
        val user2 = UserEntity()
        user1.id = 2
        user2.userName = "李四"
        user2.sex = "男"
        user2.age = 25
        user2.address = "门前小区2号楼2单元202"
        user2.phoneNumber = "17865923564"
        result.add(user2)

        // 返回列表
        return result
    }

    /**
     * 演示 @ApiImplicitParam注解
     */
    @ApiOperation("删除指定用户", notes = "根据用户Id删除指定用户")
    @ApiImplicitParam(
        name = "id",
        value = "用户Id",
        required = true, //注意，属性的默认值false会被下面@RequestParam(required = true)覆盖
        dataTypeClass = Int::class,
        paramType = "query",
        example = "123"
    )
    @GetMapping("/deleteById")
    fun deleteById(@RequestParam(required = true) id: Int): Any {

        return "成功删除指定用户！"
    }

    /**
     * 演示 @ApiImplicitParams注解
     */
    @ApiOperation("更新用户信息", notes = "根据用户Id更新用户信息")
    @ApiImplicitParams(
        ApiImplicitParam(name = "id", value = "用户Id", required = true, dataTypeClass = Int::class, paramType = "query"),
        ApiImplicitParam(name = "data", value = "用户信息", dataTypeClass = UserEntity::class, paramType = "body")
    )
    @PostMapping("/update")
    fun update(@RequestParam(required = true) id: Int, @RequestBody(required = true) data: UserEntity): Any {

        return data
    }

    /**
     * 演示 @ApiParam注解
     */
    @ApiOperation("获取指定用户", notes = "获取指定用户信息")
    @GetMapping("/getListById")
    fun getListById(@ApiParam(name = "userName", value = "用户名", required = true) userName: String): Any {

        return "获取成功！"
    }

    /**
     * 演示 @ApiIgnore注解
     */
    @ApiIgnore
    @GetMapping("/ignore")
    fun ignore(@ApiIgnore i: Int, str: String): Any {
        return "API"
    }

    /**
     * 演示 @ApiResponses注解
     */
    @ApiOperation("获取返回结果", notes = "用于演示@ApiResponses注解")
    @ApiResponses(
        ApiResponse(
            code = 200, message = "请求成功", responseHeaders = [ResponseHeader(
                name = "resultHeader",
                description = "text/plain;charset=UTF-8"
            )]
        ),
        ApiResponse(code = 400, message = "错误的请求"),
        ApiResponse(code = 401, message = "未经授权"),
        ApiResponse(code = 403, message = "访问被禁止"),
        ApiResponse(code = 404, message = "找不到页面")
    )
    @GetMapping("/getResult")
    fun getResult(): String {
        return "result"
    }
}