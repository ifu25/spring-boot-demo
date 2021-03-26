package cn.lttc.swaggerdemo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

/****************************************
 * Swagger 配置类
 * 作者：XingGang
 * 日期：2021-03-26
 * 网址：https://weiku.co
 * 邮箱：xinggang.china@gmail.com
 ****************************************/
@Configuration
open class SwaggerConfig{

    @Bean
    open fun createRestApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
            .apiInfo(getApiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("cn.lttc.swaggerdemo.web"))
            .paths(PathSelectors.any())
            .build()
    }

    /**
     * 获取接口信息
     */
    private fun getApiInfo(): ApiInfo {
        return ApiInfoBuilder()
            .title("接口文档")
            .description("这里是接口文档描述")
            .version("1.0.0")
            .contact(Contact("admin", "http://weiku.co", "xinggang.china@gmail.com"))
            .build()
    }
}