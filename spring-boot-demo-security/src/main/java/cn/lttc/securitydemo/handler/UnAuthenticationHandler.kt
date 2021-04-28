package cn.lttc.securitydemo.handler

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 认证失败处理类
 *
 * @author xinggang
 * @create 2021-04-26
 */
@Component
class UnAuthenticationHandler : AuthenticationEntryPoint {
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        response.status = 200
        response.contentType = "application/json"
        response.characterEncoding = "utf-8"
        response.writer.print("{\"msg\":\"未授权\"}")
    }
}