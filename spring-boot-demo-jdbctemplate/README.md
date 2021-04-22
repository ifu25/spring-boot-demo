# spring-boot-demo-jdbctemplate

## 一、JdbcTemplate 介绍

我们都知道使用原始的 `JDBC` 在操作数据库是比较麻烦的，所以 `Spring` 为了提高开发的效率把 `JDBC` 封装、改造了一番，而 `JdbcTemplate` 就是 `Spring` 对原始 `JDBC` 封装之后提供的一个操作数据库的工具类。

我们可以借助 `JdbcTemplate` 来完成所有数据库操作，比如：`增、删、改、查`等。

与其它流行的数据库持久化 `ORM` 框架（如`JPA`、`ibernate`）不同的是 `JdbcTemplate` 需要传统的手写 `SQL`，而不是自动生成 `SQL`。

`JdbcTemplate` 主要提供以下三种类型的方法：

- `executeXxx()`: 执行任何 SQL 语句，对数据库、表进行新建、修改、删除操作
- `updateXxx()`: 执行新增、修改、删除等语句
- `queryXxx()`: 执行查询相关的语句

### 日常开发时数据库操作框架如何选择？

实际开发过程中我们优先使用更加强大的持久化框架，比如 `MyBatis`、`MyBatis-Plus`、`Spring Data JPA` 等。

但是 `JdbcTemplate` 做为最`简单、直接、纯粹`的数据持久层方案，在某些场景上使用还是比较方便的。所以根据实际情况选择，不要有负担。

## 二、集成步骤

1、引用依赖

```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-jdbc'
```

2、数据库准备

本 demo 需要测试用数据库表 `x` 个，使用以下脚本建表：

```sql
-- 建表脚本待补充
```

2、配置文件

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://10.201.6.7:3306/demo?serverTimezone=UTC&characterEncoding=utf-8&useSSL=false
    username: demo
    password: demo
```

3、编写控制器

此处仅列出最简单几个示例，详细用法请查看代码

*HomeController*
```kotlin
//查询所有记录
val list1 = jdbc.queryForList("select * from mp_user")

//根据id查询
val list2 = jdbc.queryForList("select * from mp_user where id=?", 2)

//根据id查询（具名参数）
val map = HashMap<String, Any>()
map["id"] = 2
val list3 = paramJdbc.queryForList("select * from mp_user where id=:id", map)

//更新记录
val i = jdbc.update("update mp_user set age=age+1 where id=?", 2)
```

## 三、其它说明

> 待补充：关于框架的主要使用方法或 API 等

## 四、参考文档

> 待补充：网上比较优秀的文章