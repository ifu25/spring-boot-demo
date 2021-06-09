package cn.lttc.exceptiondemo.handler

import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 全局异常处理类
 *
 * @author xinggang
 * @create 2021-04-27
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * 请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(e: MissingServletRequestParameterException): Any {
        e.printStackTrace()
        return resMsg("请求参数异常：${e.message}")
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException): Any {
        e.printStackTrace()
        return resMsg("运行时异常")
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException::class)
    fun nullPointer(e: NullPointerException): Any {
        e.printStackTrace()
        return resMsg("NullPointer 异常，请联系管理员！")
    }

    /**
     * 其它异常，上面未匹配到的异常会在此捕获处理
     */
    @ExceptionHandler(Exception::class)
    fun handleUsernameNotFoundException(e: Exception): Any {
        e.printStackTrace()
        return resMsg("其它异常：${e.message}")
    }

    //省略对其它的异常处理...

    //region ========== 内部方法 ==========

    /**
     * 响应消息封装
     */
    private fun resMsg(msg: String): LinkedHashMap<String, Any> {
        val map = LinkedHashMap<String, Any>()
        map["code"] = -1
        map["msg"] = msg
        return map
    }

    //endregion
}