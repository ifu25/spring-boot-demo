# Spring Boot Demo 学习示例

本项目代码为📗[《Spring Boot 学习入门基础教程》](https://weiku.co/tag/spring-boot/)的演示示例。

## 项目介绍

- 🛠 开发工具：`IDEA 2020.3`
- 🌐 开发语言：`Kotlin`为主，`Java`为辅，使用 `JDK 11`
- 🙈 构建工具：`Gradle（多模块设计，公共依赖配置在 root 项目的 build.gradle）`
- 🎨 Spring Boot 版本：`2.4.x`

## 示例目录

- [spring-boot-demo-hello](./spring-boot-demo-hello) 入门示例
- [spring-boot-demo-config](./spring-boot-demo-config) 配置文件
- [spring-boot-demo-exception](./spring-boot-demo-exception) 全局异常处理
- [spring-boot-demo-jdbctemplate](./spring-boot-demo-jdbctemplate) JdbcTemplate 数据库访问
- [spring-boot-demo-template-thymeleaf](./spring-boot-demo-template-thymeleaf) Thymeleaf 模板引擎
- [spring-boot-demo-logback](./spring-boot-demo-logback) 日志配置(logback)
- [spring-boot-demo-shiro](./spring-boot-demo-shiro) Shiro 权限控制
- [spring-boot-demo-security](./spring-boot-demo-security) Spring Security 权限控制
- [spring-boot-demo-swagger](./spring-boot-demo-swagger) Swagger 接口文档
- [spring-boot-demo-redis](./spring-boot-demo-redis) Redis 缓存操作

## 文档手册

- [01-创建新的子项目的方法](./docs/01-create-new-demo.md)
- [02-编码指南与规范](./docs/02-code-guideline.md)

## 其它内容

> 💖访问[我的博客](https://weiku.co/tag/spring-boot)，查看更多 `Spring Boot` 文章教程。

## 常见问题

#### 新建子项目构建失败

提示错误：`Task 'wrapper' not found in project ':spring-boot-demo-xxx'`

解决方案：关闭 IDEA 并删除项目目录下的 `.idea` 文件夹然后重新打开。