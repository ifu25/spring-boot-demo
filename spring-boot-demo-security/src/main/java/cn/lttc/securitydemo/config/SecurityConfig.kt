package cn.lttc.securitydemo.config

import cn.lttc.securitydemo.handler.LogoutSuccessHandlerImpl
import cn.lttc.securitydemo.handler.UnAuthenticationHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.NoOpPasswordEncoder

/**
 * Spring Security 配置类
 *
 * @author xinggang
 * @create 2021-04-22
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
open class SecurityConfig : WebSecurityConfigurerAdapter() {

    /**
     * 未授权处理器
     */
    @Autowired
    lateinit var unAuthenticationHandler: UnAuthenticationHandler

    /**
     * 退出成功处理器
     */
    @Autowired
    lateinit var logoutSuccessHandlerImpl: LogoutSuccessHandlerImpl

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
            .withUser("demo").password("1111").roles("demo")
    }

    /**
     * 路径权限配置
     */
    override fun configure(http: HttpSecurity) {
        //授权路径使用 Ant 风格路径表达式
        @Suppress("ELValidationInJSP", "SpringElInspection")
        http
            //不使用 Session 禁用 CSRF，同时可解决访问 /logout 404 问题
            .csrf().disable()
            //认证失败处理类
            .exceptionHandling().authenticationEntryPoint(unAuthenticationHandler)
            //过滤请求
            .and().authorizeRequests()
            //所有用户可访问首页
            .antMatchers("/", "/logout").permitAll()
            //拥有 admin 角色才能访问 /admin
            .antMatchers("/admin").hasRole("admin")
            //需要 demo 角色
            .antMatchers("/demo").hasAnyRole("admin", "demo")
            //任意其它地址需要经过认证才能访问
            //.anyRequest().authenticated()
            //配置登录页：使用自定义的登录页，不指定 loginPage 则会使用框架提供的默认登录页，所有用户可访问
            //defaultSuccessUrl("/", true)，强制登录成功后跳转至首页
            .and().formLogin().loginPage("/login").defaultSuccessUrl("/", true).permitAll()
            //配置退出页：这里不指定自定义退出页，使用框架提供的默认退出页，所有用户可访问
            //配置了退出成功处理器时 logoutSuccessUrl("/") 将不再起作用，可以在处理器中使用 response.sendRedirect("/")
            .and().logout()
            .logoutSuccessHandler(logoutSuccessHandlerImpl)
            //.logoutSuccessUrl("/") //不能和logoutSuccessHandler同时使用，否则不会生效
            .permitAll()
    }
}