package cn.lttc.jdbctemplatedemo.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

/**
 * 首页控制器
 *
 * @author xinggang
 * @create 2021-04-21
 */
@RestController
class HomeController {

    //region ========== 全局变量 ==========

    @Autowired
    lateinit var jdbc: JdbcTemplate

    /**
     * 具名参数JdbcTemplate
     */
    @Autowired
    lateinit var paramJdbc: NamedParameterJdbcTemplate

    //endregion

    //region ========== 演示入口 ==========

    /**
     * 首页
     */
    @RequestMapping("/")
    fun index(): ModelAndView {
        val view = ModelAndView()
        view.viewName = "index"

        return view
    }

    //endregion

    //region ========== 基础 CURD ==========

    @RequestMapping("/select")
    fun select(): Any {

        //查询所有记录
        val list = jdbc.queryForList("select * from mp_user")
        println(list)

        //参数查询1：根据id查询
        val list2 = jdbc.queryForList("select * from mp_user where id=?", 2)
        println("查询id=2的用户：")
        println(list2)

        //参数查询2：根据id查询（具名参数）
        val map = HashMap<String, Any>()
        map["id"] = 2
        val list3 = paramJdbc.queryForList("select * from mp_user where id=:id", map)
        println("查询id=2的用户（具名参数）：")
        println(list3)

        //其它查询，待完善

        return list
    }

    @RequestMapping("/update")
    fun update(): Any {

        //更新年龄+1
        val i = jdbc.update("update mp_user set age=age+1 where id=?", 2)
        println("更新年龄+1影响的行数：$i")
        println("更新后的记录为：")
        println(jdbc.queryForList("select * from mp_user where id=?", 2))

        return "请查看控制台输出日志..."
    }

    //endregion

    //region ========== 高级用法 ==========

    //endregion
}