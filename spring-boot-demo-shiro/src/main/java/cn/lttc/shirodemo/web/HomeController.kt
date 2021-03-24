package cn.lttc.shirodemo.web

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authz.annotation.RequiresAuthentication
import org.apache.shiro.authz.annotation.RequiresPermissions
import org.apache.shiro.authz.annotation.RequiresRoles
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
open class HomeController {

    @RequestMapping("/")
    fun index(): Any {
        val subject = SecurityUtils.getSubject()

        //登录后显示退出链接
        val isLogin = subject.isAuthenticated
        val htmlLogout = if (isLogin) {
            "<a href='/logout'>Logout</a>"
        } else {
            ""
        }

        return " hello shiro:<br> <a href='/login.html'>Login</a> $htmlLogout <br><br>" +
                "<a href='/admin1'>/admin1</a> 登录可访问 <br>" +
                "<a href='/admin2'>/admin2</a> 验证权限admin <br>" +
                "<a href='/admin3'>/admin3</a> 验证角色admin <br>" +
                "<a href='/admin4'>/admin4</a> 注解：登录可访问 <br>" +
                "<a href='/admin5'>/admin5</a> 注解：验证权限admin <br>" +
                "<a href='/admin6'>/admin6</a> 注解：验证角色admin <br>" +
                "<a href='/admin7'>/admin7</a> 编程式权限验证 <br>"
    }

    //region ========== 配置方式 ==========

    /**
     * 配置演示：登录验证
     */
    @RequestMapping("/admin1")
    fun admin1(): Any {
        return "hello admin1"
    }

    /**
     * 配置演示：权限验证
     */
    @RequestMapping("/admin2")
    fun admin2(): Any {
        return "hello admin2"
    }

    /**
     * 配置演示：角色验证
     */
    @RequestMapping("/admin3")
    fun admin3(): Any {
        return "hello admin3"
    }

    //endregion

    //region ========== 注解方式 ==========

    /**
     * 注解演示：登录验证
     */
    @RequestMapping("/admin4")
    @RequiresAuthentication
    open fun admin4(): Any {
        return "hello admin4"
    }

    /**
     * 注解演示：权限验证
     */
    @RequestMapping("/admin5")
    @RequiresPermissions("admin")
    open fun admin5(): Any {
        return "hello admin5"
    }

    /**
     * 注解演示：角色验证
     */
    @RequestMapping("/admin6")
    @RequiresRoles("admin")
    open fun admin6(): Any {
        return "hello admin6"
    }

    //endregion

    //region ========== 编程方式 ==========

    /**
     * 编程式权限控制演示
     */
    @RequestMapping("/admin7")
    open fun admin7(): Any {
        val subject = SecurityUtils.getSubject()

        //验证登录
        if (!subject.isAuthenticated) {
            return "未登录，请先登录后再访问"
        }

        //验证角色
        //subject.hasRole("admin")

        //验证权限
        //subject.isPermitted("admin")

        return "hello admin7"
    }

    //endregion
}
















