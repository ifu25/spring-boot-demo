package cn.lttc.thymeleafdemo.web

import cn.lttc.thymeleafdemo.model.UserEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

/**
 * 首页控制器
 *
 * @author xinggang
 * @create 2021-04-20
 */
@Controller
class HomeController {

    @RequestMapping("/")
    fun index(): ModelAndView {
        //实例化视图对象
        val view = ModelAndView()

        //设置视图文件名，默认映射到 src/main/resources/templates/{viewName}.html
        view.viewName = "index"

        //设置属性
        view.addObject("title", "模板引擎演示")
        view.addObject("desc", "这里 Themeleaf 模板引擎的演示页面。")
        val user = UserEntity()
        user.userName = "admin"
        user.email = "admin@admin.com"
        user.deptName = "信息部"
        view.addObject("user", user)

        //返回视图
        return view
    }

    @RequestMapping("/demo2")
    fun demo2(): String {
        return "index"
    }

    /**
     * 循环：用户列表
     */
    @RequestMapping("/list")
    fun list(map: ModelMap): String {
        map.addAttribute("users", getUserList())

        return "list"
    }

    /**
     * 条件判断
     */
    @RequestMapping("/condition")
    fun condition(map: ModelMap): String {
        val user = UserEntity("张三", 19, "zs@lttc.cn", "信息部", "10086")
        map.addAttribute("user", user)

        return "condition"
    }

    //region ========== 内部方法 ==========

    /**
     * 获取用户列表
     */
    private fun getUserList(): List<UserEntity> {
        val list: MutableList<UserEntity> = ArrayList<UserEntity>()
        val user1 = UserEntity("张三", 18, "zs@lttc.cn", "信息部")
        val user2 = UserEntity("李四", 19, "ls@lttc.cn", "财务部")
        val user3 = UserEntity("王五", 20, "ww@lttc.cn", "营销部")
        list.add(user1)
        list.add(user2)
        list.add(user3)
        return list
    }

    //endregion
}