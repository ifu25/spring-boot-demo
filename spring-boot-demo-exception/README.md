# spring-boot-demo-exception

## 一、全局异常处理

不需要添加额外依赖，直接配置即可

```kotlin
//GlobalExceptionHandler.kt

@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException): Any {
        e.printStackTrace()
        return "运行时异常"
    }

    /**
     * 其它异常，上面未匹配到的异常会在此捕获处理
     */
    @ExceptionHandler(Exception::class)
    fun handleUsernameNotFoundException(e: Exception): Any {
        e.printStackTrace()
        return "其它异常：${e.message}"
    }
}
```