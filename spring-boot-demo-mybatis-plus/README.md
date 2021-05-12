## Spring Boot 集成 MybatisPlus 持久层框架演示

## 简介

MyBatis-Plus（简称 MP）是一个 MyBatis的增强工具，在 MyBatis 的基础上只做增强不做改变，为简化开发、提高效率而生。
内置mapper，通过少量配置即可实现大量单表的CRUD操作。

#### 资料

 - 官网地址：https://mp.baomidou.com/
 - 代码发布地址：https://github.com/baomidou/mybatis-plus
 - 文档：https://mp.baomidou.com/#/?id=%E7%AE%80%E4%BB%8B

## 项目准备

### 依赖

```DSL
    implementation "com.baomidou:mybatis-plus-boot-starter:3.4.2"
    implementation "mysql:mysql-connector-java:8.0.11"
    implementation "org.projectlombok:lombok:1.18.12"
```
### 配置

在 application.yml 配置文件中添加 mysql 数据库的相关配置：

```yml
spring:
  # 配置数据源
  datasource:
    url: jdbc:mysql://10.201.6.7:3306/demo?useSSL=false
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: demo
    password: demo
    
mybatis-plus:
  # 配置mapper(mybaits已经封装了Mapper，为了自定义动态拼接sql，引入mapper)
  mapper-locations: classpath:/mapper/mybatisplusdemo/*.xml
```

在 Spring Boot 启动类中添加 @MapperScan 注解，扫描 Mapper 接口：

```
//扫描 Mapper 接口
@MapperScan("cn.lttc.mybatisplusdemo.mapper")
```

注：此处mapper扫描共有三种方式，效果相同：
- 在启动类上添加@MapperScan("***")
- 在配置类中添加@MapperScan("***")
- 在Mapper接口上分别添加@Mapper

## 快速开始

### SQL 脚本

本示例使用 `Mysql` 数据库，需提前建表，脚本如下：

```sql
-- 创建表
CREATE TABLE mp_user
(
	id BIGINT(20) NOT NULL COMMENT '主键ID',
	name VARCHAR(30) NULL DEFAULT NULL COMMENT '姓名',
	age INT(11) NULL DEFAULT NULL COMMENT '年龄',
	email VARCHAR(50) NULL DEFAULT NULL COMMENT '邮箱',
	PRIMARY KEY (id)
);

-- 插入数据
INSERT INTO mp_user (id, name, age, email) VALUES
(1, 'Jone', 18, 'test1@baomidou.com'),
(2, 'Jack', 20, 'test2@baomidou.com'),
(3, 'Tom', 28, 'test3@baomidou.com'),
(4, 'Sandy', 21, 'test4@baomidou.com'),
(5, 'Billie', 24, 'test5@baomidou.com');
```

#### 编写PO实体类——User

```java
@Data
@TableName("mp_user")
public class User {

    private Long id;
    private String name;
    private Integer age;
    private String email;
    
}
```

#### 编写Mapper接口

```java
public interface UserMapper extends BaseMapper<User> {
    //编写复杂的sql抽象方法
}
```

#### 测试

```java
@SpringBootTest
class HelloDemoTests {

    @Resource
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- fast start ------"));
        List<User> userList = userMapper.selectList(null);
        System.out.println(userList);
    }
}
```

## PO类常用注解

### @TableName 

属性 | 类型 | 必须指定 | 默认值 | 描述
--- | --- | --- | --- | ---
value | String | 否	| "" | 表名，如果表名与PO类名一致，可以不指定

### @TableId

属性 | 类型 | 必须指定 | 默认值 | 描述
--- | --- | --- | --- | ---
value | String | 否 | "" | 主键字段名
type | Enum | 否 | IdType.NONE | 主键策略

IdType的几种类型:

1. AUTO：主键自增
2. NONE：默认值，无状态，跟随全局（一般采用这种，才配置文件中设置全局主键策略）
3. INPUT：insert前自行set主键值
4. ASSIGN_ID：使用雪花算法，分配ID(主键类型为Number(Long和Integer)或String)，如：1377880700089827329
5. ASSIGN_UUID：分配UUID,主键类型为String(since 3.3.0)  

注 （4、5适用于3.3.0之后版本）

### @TableField

属性 | 类型 | 必须指定 | 默认值 | 描述
--- | --- | --- | --- | ---
value | String | 否	| "" | 字段名，如果列名与字段名一致，可以不指定
exist | boolean | 否 | true | 是否为数据库表字段
fill | Enum | 否 | FieldFill.DEFAULT | 字段自动填充策略

```
    字段填充策略：（一般用于填充 创建时间、修改时间等字段）
        FieldFill.DEFAULT         默认不填充
        FieldFill.INSERT          插入时填充
        FieldFill.UPDATE          更新时填充
        FieldFill.INSERT_UPDATE   插入、更新时填充。
```

### @TableLogic

逻辑删除注解（应用场景讲解~）

## 新增PO的步骤

### 手动创建

以product为例：如果在项目中新增一个product实体—，构造这个结构需要哪几步？

#### 1. 创建表

 ```sql
CREATE TABLE `mp_product` (
  `proid` BIGINT NOT NULL COMMENT '商品id',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `sub_title` varchar(200) DEFAULT NULL COMMENT '商品副标题',
  `main_image` varchar(500) DEFAULT NULL COMMENT '产品主图,url相对地址',
  `price` decimal(20,2) COMMENT '价格,单位-元保留两位小数',
  `stock` int(11) COMMENT '库存数量',
  `status` int(6) DEFAULT '1' COMMENT '商品状态.1-在售 0-删除',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`proid`)
)
```

#### 2. 创建entity，添加注解

 ```java
@Data
@TableName("mp_product")
public class Product {
    @TableId
    private Long proid;
    private String name;
    private String subTitle;
    private String mainImage;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
```

#### 3. 创建Mapper

dao层，继承BaseMapper<T>，T为实体对象，包含单表的17种crud操作。

```java
public interface ProductMapper extends BaseMapper<Product> {
}
```

#### 4. 创建mapper.xml

在mapper中编写动态sql，满足复杂业务的需要（简单业务实体不用加）

 ```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="cn.lttc.mybatisplusdemo.mapper.ProductMapper">

</mapper>
```

#### 5. 创建service接口

继承IService<T>，内部调用BaseMapper进一步封装CRUD操作。简单单表crud操作由controller层直接调用，无需编写service、dao层代码

```java
 public interface ProductService extends IService<Product> {
 }
```

#### 6. 创建serviceImpl

```java
@Service
public class ProductServiceImpl extends ServiceImpl<ProductDao, Product> implements ProductService {
}
```

### 代码生成器（逆向工程）

按照上面一步步手工创建毕竟麻烦，所以考虑使用代码生成器。  
AutoGenerator 是 MyBatis-Plus 的代码生成器，通过 AutoGenerator 可以快速生成 Entity、Mapper、Mapper XML、Service、Controller 等各个模块的代码，极大的提升了开发效率。

> MP的代码生成器和Mybatis MBG代码生成器对比  

 - MP的代码生成器是基于java代码来生成，MBG基于xml文件进行生成    
 - Mybatis的代码生成器可生成：实体类、mapper接口、mapper映射文件；MP的代码生成器可生成：实体类（可以选择是否支持AR）、mapper接口、mapper映射文件、service层、controller层 
  
#### 添加依赖

```
    implementation "com.baomidou:mybatis-plus-generator:3.4.1"
    implementation "org.freemarker:freemarker:2.3.28"
```

#### 代码生成器类

示例详见 cn.lttc.mybatisplusdemo.util.CodeGenerator

## 基本CRUD操作 

### Mapper接口方法

BaseMapper 接口中封装了一系列 CRUD 常用操作，可以直接使用，而不用自定义 xml 与 sql 语句进行 CRUD 操作（当然根据实际开发需要，自定义 sql 还是有必要的）。 

> 使用详见 https://mp.baomidou.com/guide/crud-interface.html#mapper-crud-%E6%8E%A5%E5%8F%A3

### Service接口方法

IService 内部进一步封装了 BaseMapper 接口的方法；使用时，可以通过生成的 mapper 类进行CRUD操作，也可以通过生成的service的实现类进行CRUD 操作。（当然，自定义代码执行也可）

> 使用详见 https://mp.baomidou.com/guide/crud-interface.html#service-crud-%E6%8E%A5%E5%8F%A3

> demo详见 cn.lttc.mybatisplusdemo.BaseCrudTests

### 条件构造器

接口方法的参数中，会出现各种 wrapper，比如 queryWrapper、updateWrapper 等。wrapper 的作用就是用于定义各种各样的查询条件（where）。

> 使用详见 https://mp.baomidou.com/guide/wrapper.html#abstractwrapper

### AR(ActiveRecord 活动记录)模式

AR模式简单的说就是直接用实体操作数据库

#### 前提  

 - 实体类继承Model<Product>,并标识主键

```
 @Override
     protected Serializable pkVal() {
         return this.proid;
     }
```
 - mapper要继承basemapper   

#### 使用

> 方法详见 
 com.baomidou.mybatisplus.extension.activerecord.Model    
 
> demo详见 
cn.lttc.mybatisplusdemo.BaseCrudTests

## 常见应用场景

### 全局策略

#### 主键自增

```yml
mybatis-plus:
  global-config:
    db-config:
      id-type: auto
```

#### 表前缀

```
mybatis-plus:
 global-config:
  db-config:
   table-prefix: mp_
```

#### 自动填充

项目中经常会遇到一些数据，每次都使用相同的方式填充，例如记录的创建时间，更新时间等。我们可以使用MyBatis Plus的自动填充功能，完成这些字段的赋值工作：

-  数据库表中添加自动填充字段

> 在problem表中添加datetime类型的新的字段 create_time、update_time

- 在实体类上添加注解：使用@TableFile注解标注属性

```
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;//在创建时间的时候添加数据

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime; //在修改时，时间跟着修改
```

- 指定填充策略  
自定义一个类，实现 MetaObjectHandler 接口，并重写方法。添加 @Component 注解，交给 Spring 去管理。

```java

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createTime",new Date(),metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }

    @Override 
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}
```

自动填充出现时间不匹配，比当前时间晚8个小时的解决办法：  

- mysql时区不是中国标准时区，需要修改mysql的配置，强制它为中国标准时间

```
    mysql> set global time_zone='+08:00';
    mysql> set time_zone='+08:00';
    mysql> flush privileges;
```

#### 逻辑删除

删除的时候不会真正删除数据，而是不再获取这条数据；  

例如：  
- 删除: update user set deleted=1 where id = 1 and deleted=0
- 查找: select id,name,deleted from user where deleted=0

1. 在配置文件中配置全局的逻辑删除规则

```yml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-value: 1 # 逻辑已删除值(默认为 1)
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
```

2. 实体类字段上加上@TableLogic注解

```
@TableLogic
private Integer deleted;
```

 
## 插件

 使用示例：cn.lttc.mybatisplusdemo.config.MybatisPlusConfig
 
> InnerInterceptor：我们提供的插件都将基于此接口来实现功能

> 目前已有的插件功能:
  - 自动分页: PaginationInnerInterceptor
  - 多租户: TenantLineInnerInterceptor
  - 动态表名: DynamicTableNameInnerInterceptor
  - 乐观锁: OptimisticLockerInnerInterceptor
  - sql性能规范: IllegalSQLInnerInterceptor
  - 防止全表更新与删除: BlockAttackInnerInterceptor

❀ 注意:  

   使用多个功能需要注意顺序关系,建议使用如下顺序
   1. 多租户,动态表名
   2. 分页,乐观锁
   3. sql性能规范,防止全表更新与删除
  
  总结: 对sql进行单次改造的优先放入,不对sql进行改造的最后放入
    
### 分页插件

 > 作用：封装分页对象，实现物理分页  
 
```
interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
```

 使用：
 
```
 public void testPage(){
         Page<Product> param = new Page<>(1, 10);
         Page<Product> page = productService.page(param, new QueryWrapper<Product>());
         PageUtils pageUtils = new PageUtils(page);
         System.out.println(pageUtils);
     }
```

### 防止全表更新与删除

 > 作用：防止因业务bug或者漏洞可能导致的把整个表都更新或者删除的情况
 
```
interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
```
 
### 乐观锁

> 作用：当要更新一条记录时，希望这条记录没有被别人更新   

> 实现原理：  

1. 取出记录时，获取当前version  
2. 更新时，带上这个version  
3. 执行更新时，set version = yourVersion+1 where version = yourVersion，如果version不对，更新失败

> 使用：  

- 引入插件

```
 interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
```

- 在属性上添加 @Version

```
@Version
@TableField(fill = FieldFill.INSERT)
 private Integer version;
```

- 配置自动填充

```
this.setFieldValByName("version",1,metaObject);
```

- 测试

```
    @Test
    public void updateData1(){

        //模拟并发，demo1成功，demo2失败
        Demo demo1 = demoService.getById(1385027232805748738L);
        Demo demo2 = demoService.getById(1385027232805748738L);
        demo1.setAge(70);
        demo2.setAge(22);
        demoService.updateById(demo1);
        demoService.updateById(demo2);

    }
```