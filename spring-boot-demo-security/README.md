# spring-boot-demo-security

## TODO

- 自定义 UserDetailsService https://blog.csdn.net/liuminglei1987/article/details/107605953
- 资源权限动态控制（FilterSecurityInterceptor） https://blog.csdn.net/liuminglei1987/article/details/107606012

## 一、Spring Security 介绍

Spring Security 是一个功能强大且高度可定制的身份验证和访问控制框架。

类似的比较知名的框架还有 `shiro`，两者对比可参考此文：https://www.cnblogs.com/zoli/p/11236799.html

框架所做的核心的两大功能是：

- `认证`，就是我们常说的登录
- `授权`，就是用户是否有权限进行某项操作

## 二、集成步骤

1、引用依赖

```groovy
implementation 'org.springframework.boot:spring-boot-starter-security'
```

2、创建配置类：`SecurityConfig`

>   最终以项目类文件中的代码为准，此处仅做演示

```kotlin
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
open class SecurityConfig : WebSecurityConfigurerAdapter() {

    /**
     * 认证配置
     */
    override fun configure(auth: AuthenticationManagerBuilder) {
        @Suppress("DEPRECATION")
        auth
            //使用内存中的 InMemoryUserDetailsManager
            .inMemoryAuthentication()
            //密码编码器（为了演示这里不使用编码器，生产环境强烈建议使用 BCryptPasswordEncoder 等编码器）
            .passwordEncoder(NoOpPasswordEncoder.getInstance())
            //增加一个用户
            .withUser("admin").password("1111").roles("admin")
            .and()
            //再增加一个用户
            .withUser("test").password("1111").roles("test")
    }

    /**
     * 访问路径的权限配置
     */
    override fun configure(http: HttpSecurity) {
        //授权路径使用 Ant 风格路径表达式
        @Suppress("ELValidationInJSP", "SpringElInspection")
        http
            .authorizeRequests()
            //所有用户可访问首页
            .antMatchers("/").permitAll()
            //拥有 admin 角色才能访问 /admin
            .antMatchers("/admin").hasRole("admin")
            //需要 test 角色
            .antMatchers("/test").access("hasRole('test')")
            //任意其它地址需要经过认证才能访问
            .anyRequest().authenticated()
            .and()
            //登录设置，所有用户可访问
            .formLogin()
            //这里使用默认登录页，所以注释掉
            //.loginPage("/login")
            .permitAll()
            .and()
            //退出设置，所有用户可访问
            .logout()
            //这里使用默认退出页，所以注释掉
            //.logoutUrl("/logout")
            .permitAll()
    }
}
```

解读：

- 继承 `WebSecurityConfigurerAdapter`，并重写它的方法来配置一些安全的细节
- `configure(auth: AuthenticationManagerBuilder)` 认证配置，这里创建了两个用户用于测试
- `configure(http: HttpSecurity)` 用来配置哪些路径需要被保护，哪些路径不需要保护等

3、编写控制器

请参考：[HomeController.kt](./src/main/java/cn/lttc/securitydemo/web/HomeController.kt)

4、浏览运行

http://127.0.0.1:8080

## 三、常用配置

### 3.1 不拦截静态资源

```java
@Override
public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/css/**", "/js/**", "/plugins/**", "/images/**", "/fonts/**");
}
```

### 3.2 认证失败处理

默认认证失败会返回默认的 403 错误页面，如果想自定义认证失败的处理逻辑可按如下方法

3.2.1 定义认证失败处理类：

```kotlin
//UnAuthenticationHandler.kt

@Component
class UnAuthenticationHandler : AuthenticationEntryPoint {
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        response.status = 200
        response.contentType = "application/json"
        response.characterEncoding = "utf-8"
        response.writer.print("{\"msg\":\"没有权限\"}")
    }
}
```

3.2.2 配置使用：

```kotlin
//SecurityConfig.kt

open class SecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var unAuthenticationHandler: UnAuthenticationHandler

    /**
     * 路径权限配置
     */
    override fun configure(http: HttpSecurity) {
        //认证失败处理类
        http.exceptionHandling().authenticationEntryPoint(unAuthenticationHandler)
    }
}
```

### 3.3 Thymeleaf 模板标签支持

Spring Security 可以在一些视图技术中进行控制显示效果，在使用 Thymeleaf 渲染前端的 html 时，Thymeleaf 为 Spring Security 提供的标签属性。

3.3.1 首先需要引入`thymeleaf-extras-springsecurity` 依赖支持，目前最新版本为 `5`，添加依赖如下：

```groovy
//模板权限标签属性支持
implementation 'org.thymeleaf.extras:thymeleaf-extras-springsecurity5:3.0.4.RELEASE'
//模板引擎
implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
```

3.3.2 模板增加 xml 命名空间

```html
<html xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity5">
```

3.3.3 模板中使用

```html
<!--当前登录用户信息-->
<div>当前用户：
    <span sec:authorize="isAuthenticated()">
        <span sec:authentication="name"></span>
        <span sec:authentication="principal.authorities"></span>
        <a href='/logout'>【退出】</a>
    </span>
    <span sec:authorize="!isAuthenticated()">
        <a href="/login">未登录(点击登录)</a>
    </span>
</div>
```

请参考：[_header.html](./src/main/resources/templates/layout/_header.html)

### 3.4 使用注解实现权限控制

3.4.1 配置

配置文件参加 `@EnableGlobalMethodSecurity(prePostEnabled = true)` 注解

```kotlin
//SecurityConfig.kt
@EnableGlobalMethodSecurity(prePostEnabled = true)
open class SecurityConfig : WebSecurityConfigurerAdapter() {
 //其它代码省略   
}
```

3.4.2 使用

控制器中使用如下：

```kotlin
@RequestMapping("admin2")
@PreAuthorize("hasRole('admin')") //拥有 admin 角色可访问
open fun admin2(): ModelAndView {
    val view = ModelAndView("demo")
    view.addObject("content", "这里是 admin 页，只有 admin 角色可以访问。")
    return view
}

@RequestMapping("demo2")
@PreAuthorize("hasAnyRole('admin','demo')") //拥有 admin 或 demo 仍一角色可访问
open fun demo2(): ModelAndView {
    val view = ModelAndView("demo")
    view.addObject("content", "这里是 demo 页，admin 和 demo 角色都可以访问。")
    return view
}
```

其它语法举例

```java
/**
 * anyRequest          |   匹配所有请求路径
 * access              |   SpringEl表达式结果为true时可以访问
 * anonymous           |   匿名可以访问
 * denyAll             |   用户不能访问
 * fullyAuthenticated  |   用户完全认证可以访问（非remember-me下自动登录）
 * hasAnyAuthority     |   如果有参数，参数表示权限，则其中任何一个权限可以访问
 * hasAnyRole          |   如果有参数，参数表示角色，则其中任何一个角色可以访问
 * hasAuthority        |   如果有参数，参数表示权限，则其权限可以访问
 * hasIpAddress        |   如果有参数，参数表示IP地址，如果用户IP和参数匹配，则可以访问
 * hasRole             |   如果有参数，参数表示角色，则其角色可以访问
 * permitAll           |   用户可以任意访问
 * rememberMe          |   允许通过 remember-me 登录的用户访问
 * authenticated       |   用户登录后可访问
 */
```

## 四、觉见问题

### 4.1 注解权限无效

kotlin 中需要将方法标识为 `open`，如：

```kotlin
//注意第 4 行的 open 不能少
@RequestMapping("demo2")
@PreAuthorize("hasAnyRole('admin','demo')")
open fun demo2(): ModelAndView {
    val view = ModelAndView("demo")
    view.addObject("content", "这里是 demo 页，admin 和 demo 角色都可以访问。")
    return view
}
```

## 五、参考文档

> Spring Boot 安全框架 Spring Security 入门：https://www.iocoder.cn/Spring-Boot/Spring-Security/