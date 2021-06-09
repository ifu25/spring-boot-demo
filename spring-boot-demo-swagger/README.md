# spring-boot-demo-swagger

## 一、Swagger 简介

`Swagger` 是一个规范和完整的API框架，用于生成、描述、调用和可视化 RESTful 风格的 Web 服务。

总体目标是使客户端和文件系统作为服务器以同样的速度来更新。文件的方法、参数和模型紧密集成到服务器端的代码，允许API来始终保持同步。

- 详细资料可以浏览官网：https://swagger.io/

## 二、Spring Boot集成 Swagger生成 API 文档

由于Spring的流行，Marty Pitt编写了一个基于Spring的组件`swagger-springmvc`，用于将`swagger`集成到`springmvc`中来。而`springfox`则是从这个组件发展而来，同时`springfox`也是一个新的项目，我们使用`springfox`项目中的一个组件`springfox-swagger2` ，以及简单注解即可生成`swagger`的API文档。

`springfox-swagger2`依赖于OAS(`OpenAPI Specification`，OAS)规范文档，其中OAS本身是一个API规范，它用于描述一整套API接口，包括一个接口是GET还是POST请求，有哪些参数哪些header等信息都会被包括在这个`json文件`中。而这个组件的功能就是帮助我们自动生成这个`json文件`，另外，我们还会用到的另外一个组件`springfox-swagger-ui`就是将这个`json文件`解析出来，用一种更友好的方式呈现出来。

### 使用步骤

1. **引入依赖**

   ```groovy
   implementation('io.springfox:springfox-swagger2:2.9.2') //Swagger 依赖
   implementation('io.springfox:springfox-swagger-ui:2.9.2') //Swagger UI 依赖，以实现 API 接口的 UI 界面
   ```

2. **编写Swagger配置文件： `SwaggerConfiguration`类**

   因为 `Spring Boot` 暂未提供 `Swagger` 内置的支持，所以我们需要自己定义配置类。

   ```kotlin
   @Configuration
   @EnableSwagger2 //Swagger的开关，表示已经启用Swagger
   open class SwaggerConfig {
   
       @Bean
       open fun createRestApi(): Docket {
           return Docket(DocumentationType.SWAGGER_2) //文档类型，使用Swagger2
               .apiInfo(getApiInfo()) //设置 API 信息
               .host("localhost:9090") //不配置，默认当前项目端口
               .pathMapping("/") //地址映射
               .groupName("1.0") //分组名称
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
   ```

   `@EnableSwagger2`注解： 标记项目启用 Swagger API 接口文档。

   `createRestApi()` 方法：配置Swagger2核心配置Docket摘要。

   **Docket 常用属性**

   `apiInfo`： 用于设置API文档 信息。

   `host`：API 接口测试的 URL 地址及端口，不设置时，默认当前项目的地址及端口。

   `pathMapping`：用于将servlet路径映射，如果设置了此项，则会添加到API接口测试的基本路径。

   `groupName`：分组名称。

   `select`：启动用于API选择的构建器。 不过要完成API选择器的构建，需要调用API选择器的构建方法，这将在调用build方法时自动回退到构建摘要。

   `apis`：API选择器的构建方法。用于选择监控的package。

   `paths`：用于设置监控的路径。

   **ApiInfo 常用属性**

   `title`：设置文档页标题。

   `description`：设置文档的详细信息。

   `version`：设置文档版本号。

   `contact`：设置联系人信息。

   `termOfServiceUrl`：设置连接网站地址。

3. **创建测试实例**

   **`UserEntity`模型**

   ```kotlin
   class UserEntity {
   
       //用户名
       var UserName: String? = null
   
       //性别
       var sex: String? = null
   
       //年龄
       var age: Int? = null
   
       //地址
       var address: String? = null
   
       //联系方式
       var phoneNumber: String? = null
   }
   ```

   **`UserController`控制器**

   ```Kotlin
   @RestController
   @Api(tags = ["用户接口"])
   class UserController {
   
       /**
        * 获取用户列表
        */
       @GetMapping("/getList")
       @ApiOperation("获取用户列表")
       fun getList(): Any {
           // 查询列表
           val result = ArrayList<UserEntity>()
           val user = UserEntity()
           user.UserName = "张三"
           user.sex = "男"
           user.age = 20
           user.address = "状元府小区1号楼1单元101"
           user.phoneNumber = "18656231478"
           result.add(user)
   
           // 返回列表
           return result
       }
   }
   ```

   **应用程序类**

   ```Kotlin
   @SpringBootApplication
   public class SwaggerDemo {
       public static void main(String[] args) {
           SpringApplication.run(SwaggerDemo.class, args);
       }
   
   }
   ```

4. **启动测试**

   执行 Application 启动项目。然后浏览器访问 `http://127.0.0.1:9090/swagger-ui.html` 地址，就可以看到 Swagger 生成的 API 接口文档。结果如下图：

   ![](http://img.weiku.co/md/20210417112238.jpg)

   在 Swagger 的 UI 界面上，提供了简单的测试接口的工具。我们仅仅需要点开某个接口，点击「Try it out」按钮。

5. **扩展：`springfox升级swagger-ui 3.0.0版本`**

   （1）引用依赖

   `build.gradle`中直接使用以下依赖

   ```Kotlin
   implementation('io.springfox:springfox-boot-starter:3.0.0')
   ```

   替换掉原先的依赖引用

   ```Kotlin
   implementation('io.springfox:springfox-swagger2:2.9.2') //Swagger 依赖
   implementation('io.springfox:springfox-swagger-ui:2.9.2') //Swagger UI 依赖，以实现 API 接口的 UI 界面
   ```

   （2）Swagger配置

   直接在应用程序类中添加`@EnableOpenApi`注解，删掉原先设置的Swagger配置文件`SwaggerConfig`类。

   ```Kotlin
   @EnableOpenApi
   @SpringBootApplication
   public class SwaggerDemo {
   
       public static void main(String[] args) {
           SpringApplication.run(SwaggerDemo.class, args);
       }
   
   }
   ```

   其余的不需要修改，执行 Application 启动项目。然后浏览器访问 `http://127.0.0.1:9090/swagger-ui/index.html` 地址，就可以看到 Swagger 生成的 API 接口文档。结果如下图：

   ![](http://img.weiku.co/md/20210417124140.jpg)

## 三、 Swagger 生成 API 文档 的增强实现 knife4j

因 `Swagger` 默认的 `UI` 实现不是很好，所以建议使用 `knife4j`。

`Knife4j`的前身是`swagger-bootstrap-ui`，前身`swagger-bootstrap-ui`是一个纯`swagger-ui`的`ui`皮肤项目。

项目初衷是为了写一个增强版本的`swagger的前端ui`，但是随着项目的发展，面对越来越多的个性化需求，不得不编写后端Java代码以满足新的需求，在`swagger-bootstrap-ui`的1.8.5~1.9.6版本之间，采用的是后端Java代码和UI都混合在一个Jar包里面的方式提供给开发者使用，这种方式虽说对于集成swagger来说很方便，只需要引入jar包即可，但是在微服务架构下显得有些臃肿。

因此，项目正式更名为**`knife4j`**，取名knife4j是希望她能像一把匕首一样小巧，轻量,并且功能强悍，更名也是希望把她做成一个为Swagger接口文档服务的通用性解决方案，不仅仅只是专注于前端UI。`swagger-bootstrap-ui`的所有特性都会集中在`knife4j-spring-ui`包中，并且后续也会满足开发者更多的个性化需求。主要的变化是：

1. 项目的相关类包路径更换为`com.github.xiaoymin.knife4j`前缀，开发者使用增强注解时需要替换包路径
2. 后端Java代码和UI包分离为多个模块的jar包，以面对在目前微服务架构下，更加方便的使用增强文档注解(使用`SpringCloud`微服务项目，只需要在网关层集成UI的jar包即可，因此分离前后端)。

### 使用步骤

1. **引入依赖**

   ```groovy
   //使用 swagger 的增强实现 knife4j
   implementation 'com.github.xiaoymin:knife4j-spring-boot-starter:3.0.2'
   ```

2. **创建Swagger配置依赖**

   ```Kotlin
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
   ```

3. **创建测试实例**

   `HomeController.kt`包含一个简单的RESTful接口,代码示例如下：

   ```Kotlin
   @RestController
   @Api(tags = ["首页"])
   class HomeController {
   
       @GetMapping("/")
       @ApiOperation("获得指定用户编号的用户", notes = "目前仅作为测试，所以返回一个字符串")
       fun index(): Any {
           return "Hello Swagger + knife4j！<a href= '/doc.html'>点此浏览文档>></a>"
       }
   }
   ```

4. **启动测试**

   执行 Application 启动项目。然后浏览器访问 `http://127.0.0.1:9090/doc.html` 地址，界面效果图如下：

   ![](http://img.weiku.co/md/20210419142214.jpg)

选择`文档卡片页`查看API接口的详细信息，包括请求类型、请求参数、响应参数等；选择`调试卡片页`测试API接口的执行情况。

### knife4j官方文档

参考文档手册：https://doc.xiaominfo.com/knife4j/documentation/get_start.html

## 四、Swagger 注解

**swagger通过注解生成接口文档，包括接口名、请求方法、参数、返回信息等。**

**源码**：https://github.com/swagger-api/swagger-core/tree/1.5/modules/swagger-annotations/src/main/java/io/swagger/annotations

**常用注解列表如下：**

```groovy
@Api //：修饰整个类，描述Controller的作用
@ApiOperation //：描述一个类的一个方法，或者说一个接口
@ApiImplicitParam //：一个请求参数
@ApiImplicitParams //：多个请求参数
@ApiParam //：单个参数描述
@ApiIgnore //：使用该注解忽略这个API
@ApiModel //：用对象实体来作为入参
@ApiModelProperty //：用对象接实体收参数时，描述对象的一个字段
@ApiResponse //：HTTP响应其中1个描述
@ApiResponses //：HTTP响应整体描述
```

### `@Api`

**添加在 Controller 类上，标记它作为 Swagger 文档资源。**

```kotlin
@Api(tags = ["首页"])
```

**常用属性**

- `tags`属性：对实现相同业务功能的接口类做一个大型的分组，该分组中包括同业务功能下的所有的接口。`[]` 数组，可以填写多个。

  > **注意**：
  >
  > 一个Controller的`@Api`的`tags`属性设置多个标签，则这个Controller下的API接口就会出现在多个标签中。
  >
  > 多个Controller的`@Api`的`tags`属性设置同一个标签，则这些Controller下的API接口仅会出现在这一个标签中。
  >
  > 本质上，`tags` 就是为了分组 API 接口，和 Controller 本质上是一个目的。所以绝大数场景下，我们只会给一个 Controller 设置一个**唯一**的标签。

- `hidden` 属性：是否隐藏，不再 API 接口文档中显示。**此属性不起作用，可使用`@ApiIgnore`注解代替。**

### `@ApiOperation`

**添加在 Controller 方法上，标记它是一个 API 操作。**

```kotlin
 @ApiOperation("获得指定用户编号的用户", notes = "目前仅作为测试，所以返回一个字符串")
```

**常用属性**

- `value` 属性：API 操作名。
- `notes` 属性：API 操作的描述。

**不常用属性**

- `tags` 属性：和`@Api`注解的`tags`属性一致。若二者不一致，则此接口会出现在其`tags`属性对应的标签下。

- `nickname` 属性：API 操作接口的唯一标识，主要用于和第三方工具做对接。若同一个Controller不同API的`nickname`属性配置相同，则API操作接口标识会自动添加数字下标。
- `httpMethod` 属性：请求方法，可选值为 `GET`、`HEAD`、`POST`、`PUT`、`DELETE`、`OPTIONS`、`PATCH` 。因为 Swagger 会解析`SpringMVC`的注解，所以一般无需填写。
- `produces` 属性：和`@Api` 注解的`produces`属性一致，可以配置多个。请求请求头的**可接受类型**( [Accept](https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Accept) )
- `consumes` 属性：和`@Api`注解的`consumes`属性一致，可以配置多个。请求请求头的**提交内容类型**( [Content-Type](https://juejin.im/post/5cb34fc06fb9a068a75d3555) )
- `authorizations` 属性：和`@Api`注解的`authorizations`属性一致。授权相关的配置，`[]` 数组，使用 [`@Authorization`](https://github.com/swagger-api/swagger-core/blob/1.5/modules/swagger-annotations/src/main/java/io/swagger/annotations/Authorization.java) 注解。
- `hidden` 属性：和`@Api`注解的`hidden`属性一致。**可使用`@ApiIgnore`注解代替**。

### `@ApiImplicitParam`

**添加在 Controller 方法上，声明每个请求参数的信息。**

```Kotlin
ApiImplicitParam(name = "id", value = "用户Id", required = true, dataTypeClass = Int::class)
```

**常用属性**

- `name` 属性：参数名。

- `value` 属性：参数的简要说明。

- `required` 属性：是否为必传参数。默认为 `false` 。

- `dataType` 属性：数据类型，通过字符串 String 定义，可以是类名或原始数据类型。

- `dataTypeClass` 属性：数据类型，通过 `dataTypeClass` 定义。在设置了 `dataTypeClass` 属性的情况下，会覆盖 `dataType` 属性。**推荐采用这个方式**。

- `paramType`属性：参数所在位置的类型。默认为 `“query”` 。**绝大多数情况下，使用 `"query"` 值这个类型即可。**

  总共有如下 5 种方式：

    - `"query"` 值：对应 `SpringMVC` 的 `@RequestParam`  注解，直接跟参数完成自动映射赋值。
    - `"body"` 值：对应 `SpringMVC` 的 `@RequestBody` 注解，以流的形式提交 仅支持POST。
    - `"path"` 值：对应 `SpringMVC` 的 `@PathVariable` 注解，以地址的形式提交数据。
    - `"header"` 值：对应 `SpringMVC` 的 `@RequestHeader` 注解，参数在request headers 里边提交。
    - `"form"` 值：以form表单的形式提交， 仅支持POST，不常用。

- `example` 属性：非请求体(body)参数的单个请求示例。

- `examples` 属性：参数值的复杂示例值，使用 [`@Example`](https://github.com/swagger-api/swagger-core/blob/1.5/modules/swagger-annotations/src/main/java/io/swagger/annotations/Example.java) 注解。

### `@ApiImplicitParams`

**添加在 Controller 方法上，表示对参数的说明，`@ApiImplicitParams`为一组`@ApiImplicitParam`。**

```Kotlin
@ApiImplicitParams(
        ApiImplicitParam(name = "id", value = "用户Id", dataTypeClass = Int::class, paramType = "query"),
        ApiImplicitParam(name = "data", value = "用户信息", dataTypeClass = UserEntity::class, paramType = "body")
    )
```

### `@ApiParam`

**用在请求方法中，描述参数信息，是单个参数的描述。**

```kotlin
fun getListById(@ApiParam(name = "userName", value = "用户名", required = true) userName: String): Any
```

### `@ApiIgnore`

**添加在类、方法上、方法参数中，用来屏蔽某些接口或参数，使其不在页面上显示。**

```kotlin
//作用在类上
@ApiIgnore
class UserController {}

//作用在方法上
@ApiIgnore
@GetMapping("/ignore")
fun ignore(): Any {
    return "API"
}

//作用在方法参数中
fun ignore(@ApiIgnore i: Int, str: String): Any
```

### `@ApiModel`

**添加在接口相关实体类上，用来对接口相关实体类添加额外的描述信息。**

```Kotlin
@ApiModel(value = "用户实体",description = "表示用户对象信息")
class UserEntity {}
```

**常用属性**

- `value` 属性：Model 名字。
- `description` 属性：Model 描述。

### `@ApiModelProperty`

**添加在接口相关实体类的参数上的注解，用来对具体的接口相关实体类中的参数添加额外的描述信息。**

```kotlin
 @ApiModelProperty(value = "用户Id", required = true, example = "张三")
 var userName: String? = null
```

**常用属性**

- `value` 属性：属性简要说明。
- `required` 属性：是否为必传参数，false：非必传参数； true：必传参数。
- `dataType` 属性：参数的数据类型，可以是类名或原始数据类型，如`java.lang.String`，此值将覆盖从类属性读取的数据类型。
- `example` 属性：属性的示例值。

**不常用属性**

- `hidden` 属性：隐藏模型属性，false：不隐藏； true：隐藏。
- `name` 属性：覆盖成员变量的名字，使用该属性进行自定义。

> **注意**：实体类属性需要遵循驼峰命名规则，否则`@ApiModelProperty`注解不起作用。

### `@ApiResponse`

**添加在 Controller 类的方法上，用来对该接口方法的一个或多个返回值进行描述，一般不会单独使用，常常和 `@ApiResponses` 注解配合使用。**

```kotlin
@ApiResponse(code = 200, message = "请求成功")
```

**常用属性**

- `code` 属性：描述接口返回的响应数据的状态码，如 200、404等。 `必填`
- `message` 属性：对接口返回的响应数据的状态码进行描述。`必填`
- `responseHeaders` 属性：对接口的返回头做一个描述。

### `@ApiResponses`

**添加在 Controller 类的方法上，作用和 `@ApiResponse` 注解相同，只是在当有多个 `@ApiResponse` 注解同时存在时才会使用该注解。**

```kotlin
@ApiResponses(
        ApiResponse(code = 200, message = "请求成功"),
        ApiResponse(code = 400, message = "错误的请求"),
        ApiResponse(code = 401, message = "未经授权"),
        ApiResponse(code = 403, message = "访问被禁止"),
        ApiResponse(code = 404, message = "找不到页面")
    )
```

**常用属性**

- `value` 属性：对接口的返回状态码及其返回状态码描述进行多条描述，来说明该接口的返回格式有多条额外的描述。

> **注意**：`ApiResponses` 注解不能单独使用，因为他只有一个类型为 `ApiResponse` 数组形式的 value 属性，即要使用 `ApiResponses` 注解就必须要填充 value 属性。

### 参考文档

1. [芋道源码 Spring Boot API 接口文档 Swagger 入门](https://www.iocoder.cn/Spring-Boot/Swagger/)
2. [IT 学院 Swagger 入门教程](https://book.itxueyuan.com/962W/KXEjL)


