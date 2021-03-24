package cn.lttc.securitydemo.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.NoOpPasswordEncoder

/****************************************
 * Spring Security 配置
 * 作者：XingGang
 * 日期：2021-03-24
 * 网址：https://weiku.co
 * 邮箱：xinggang.china@gmail.com
 ****************************************/
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