package cn.lttc.securitydemo.handler

import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 退出登录处理类
 *
 * @author xinggang
 * @create 2021-04-27
 */
@Component
class LogoutSuccessHandlerImpl : LogoutSuccessHandler {

    override fun onLogoutSuccess(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication?) {
        println("onLogoutSuccess:成功退出")
        //重定向到首页
        response.sendRedirect("/")
    }
}