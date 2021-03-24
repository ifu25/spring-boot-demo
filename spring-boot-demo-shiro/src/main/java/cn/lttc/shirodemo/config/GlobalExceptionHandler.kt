package cn.lttc.shirodemo.config

import org.apache.shiro.authz.AuthorizationException
import org.apache.shiro.authz.UnauthorizedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody

/****************************************
 * 全局异常处理
 * 作者：XingGang
 * 日期：2021-03-24
 * 网址：https://weiku.co
 * 邮箱：xinggang.china@gmail.com
 ****************************************/
@ControllerAdvice
@ResponseBody
open class GlobalExceptionHandler {

    /**
     * shiro 认证失败异常
     */
    @ExceptionHandler(AuthorizationException::class)
    open fun authorizationException(ex: Exception?): String? {
        return "认证失败"
    }

    /**
     * shiro 未授权异常
     */
    @ExceptionHandler(UnauthorizedException::class)
    open fun handleShiroException(ex: Exception?): String? {
        return "无权限"
    }
}