package cn.lttc.securitydemo.web

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

/**
 * 首页控制器
 *
 * @author xinggang
 * @create 2021-04-22
 */
@RestController
open class HomeController {

    /**
     * 首页
     */
    @RequestMapping("/")
    fun index(): ModelAndView {
        return ModelAndView("index")
    }

    /**
     * 管理页：拥有 admin 角色的用户可访问（配置文件定义权限）
     */
    @RequestMapping("admin")
    open fun admin(): ModelAndView {
        val view = ModelAndView("demo")
        view.addObject("content", "这里是 admin 页，只有 admin 角色可以访问。")
        return view
    }

    /**
     * 管理页：拥有 admin 角色的用户可访问（注解方式定义权限）
     */
    @RequestMapping("admin2")
    @PreAuthorize("hasRole('admin')")
    open fun admin2(): ModelAndView {
        val view = ModelAndView("demo")
        view.addObject("content", "这里是 admin 页，只有 admin 角色可以访问。")
        return view
    }

    /**
     * 测试页：拥有 admin、demo 任一角色的用户可访问（配置文件定义权限）
     */
    @RequestMapping("demo")
    open fun demo(): ModelAndView {
        val view = ModelAndView("demo")
        view.addObject("content", "这里是 demo 页，admin 和 demo 角色都可以访问。")
        return view
    }

    /**
     * 测试页：拥有 admin、demo 任一角色的用户可访问（注解方式定义权限）
     */
    @RequestMapping("demo2")
    @PreAuthorize("hasAnyRole('admin','demo')")
    open fun demo2(): ModelAndView {
        val view = ModelAndView("demo")
        view.addObject("content", "这里是 demo 页，admin 和 demo 角色都可以访问。")
        return view
    }

    /**
     * 匿名访问：不登录可访问，登录后不能访问
     */
    @RequestMapping("anonymous")
    @PreAuthorize("isAnonymous()")
    open fun anonymous(): ModelAndView {
        val view = ModelAndView("demo")
        view.addObject("content", "这里是 anonymous 页，未登录可访问，登录后不能访问")
        return view
    }

    /**
     * 所有人可访问：没有注解，配置已放行
     */
    @RequestMapping("guest")
    open fun guest(): ModelAndView {
        val view = ModelAndView("demo")
        view.addObject("content", "这里是 guest 页，所有人都可访问")
        return view
    }

    /**
     * 自定义登录页
     */
    @RequestMapping("login")
    fun login(): ModelAndView {
        return ModelAndView("login")
    }

    /**
     * 获取当前用户信息
     */
    @RequestMapping("userInfo")
    fun userInfo(map: ModelMap): ModelAndView {

        val userName = SecurityContextHolder.getContext().authentication.name

        val view = ModelAndView("userInfo")
        view.addObject("userName", userName)
        return view
    }
}