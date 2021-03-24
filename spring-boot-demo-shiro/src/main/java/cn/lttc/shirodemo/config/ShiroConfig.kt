package cn.lttc.shirodemo.config

import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

/****************************************
 * Shiro 配置类
 * 作者：XingGang
 * 日期：2021-03-20
 * 网址：https://weiku.co
 * 邮箱：xinggang.china@gmail.com
 ****************************************/
@Configuration
open class ShiroConfig {

    @Bean
    open fun shiroFilterFactory(securityManager: DefaultWebSecurityManager): ShiroFilterFactoryBean {
        val factoryBean = ShiroFilterFactoryBean()
        factoryBean.securityManager = securityManager

        //权限配置，另外也可以在方法上使用注解，参考 HomeController
        val map = HashMap<String, String>()
        map["/admin1"] = "authc"
        map["/admin2"] = "perms[admin]"
        map["/admin3"] = "roles[admin]"
        factoryBean.filterChainDefinitionMap = map

        //未认证时跳转的登录页
        factoryBean.loginUrl = "/login.html"

        return factoryBean
    }

    /**
     * 配置 Shiro 核心的安全管理器
     */
    @Bean
    open fun defaultWebSecurityManager(shiroRealm: ShiroRealm): DefaultWebSecurityManager {
        val manager = DefaultWebSecurityManager()
        //将自定义的realm交给SecurityManager管理
        manager.setRealm(shiroRealm)
        return manager
    }

    /**
     * 自定义的 Realm 实现，用于完成`认证`和`授权`
     */
    @Bean
    open fun shiroRealm(): ShiroRealm {
        return ShiroRealm()
    }

    /**
     * 开启 Shiro 的注解支持
     */
    @Bean
    open fun authorizationAttributeSourceAdvisor(securityManager: DefaultWebSecurityManager): AuthorizationAttributeSourceAdvisor {
        val attributeSourceAdvisor = AuthorizationAttributeSourceAdvisor()
        attributeSourceAdvisor.securityManager = securityManager
        return attributeSourceAdvisor
    }

    @Bean
    @ConditionalOnMissingBean
    open fun defaultAdvisorAutoProxyCreator(): DefaultAdvisorAutoProxyCreator {
        val defaultAAP = DefaultAdvisorAutoProxyCreator()
        defaultAAP.isProxyTargetClass = true
        return defaultAAP
    }
}