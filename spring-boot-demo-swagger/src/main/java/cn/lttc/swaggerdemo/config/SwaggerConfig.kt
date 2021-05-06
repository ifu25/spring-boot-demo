package cn.lttc.swaggerdemo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

/**
 * Swagger 配置类
 *
 * @author 谢  鑫
 * @create 2021-04-16
 */

@Configuration
open class SwaggerConfig {

    @Bean
    open fun createRestApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2) //文档类型，使用Swagger2
            .apiInfo(getApiInfo()) //设置 API 信息
            .host("localhost:9090") //不配置，默认当前项目端口
            .pathMapping("/") //地址映射
            .groupName("1.0")
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
            .termsOfServiceUrl("http://www.baidu.com")
            .build()
    }
}