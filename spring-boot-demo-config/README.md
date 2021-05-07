# spring-boot-demo-config

## 一、Spring Boot 配置文件介绍

`Spring Boot` 项目中内置了一个习惯性的配置来开启各个功能模块的默认配置，无需手动进行配置，这就是我们所说的全局配置文件。在 `Spring Boot` 项目中，官方推荐使用 `properties` 或者 `yaml` 文件来完成配置。

- 默认位置： `src/main/resources` 目录下。

### YAML 文件

`yaml`文件以类似大纲的缩进形式来表示，配置信息利用阶梯化缩进的方式，其结构显得清晰易读，但它存在一些语法规则：

- 大小写敏感
- 缩进表示层级
- 缩进只能使用空格
- 空格的数量不重要，但是相同层级的元素要左侧对齐

### Properties 文件

`properties` 配置文件以键值对的形式来表示，简单易用，但是在配置复杂结构时不如` yaml` 优雅美观。

## 二、配置文件中的属性设置

在Spring Boot的配置文件中，不仅可以设置各个Starter模块中预定义的配置属性，也可以设置一些我们自定义的配置属性。

### 预定义属性

1. 在 `application.yml`配置文件中的模块预定义属性设置方式如下：

   ```groovy
   # 服务信息
   server:
     port: 9090
   ```

   可以看出，配置信息利用阶梯化缩进的方式，其结构显得更为清晰易读，同时配置内容的字符量也得到显著的减少。

2. 在 `application.properties` 配置文件中的模块预定义属性设置方式如下：

   ```groovy
   # 服务信息
   server.port = 9090
   ```

### 自定义属性

在使用Spring Boot开发应用时，我们可以发现自定义属性是有高亮背景的，鼠标放上去，有一个`Cannot resolve configuration property`的配置警告。虽然不影响属性的使用，但看着也是很不舒服的。

解决这个警告有两种方式：一是通过设置去除高亮设置(不建议)；二是可以通过完善配置元数据的方式。

**使用步骤**

1. 创建一个配置类 `PersonValue.java`，定义一个自定义配置

   ```java
   @Component
   @ConfigurationProperties(prefix = "person")
   public class PersonValue {
       private String name;
       private Integer age;
   
       public String getName() {
           return name;
       }
       public void setName(String name) {
           this.name = name;
       }
       public Integer getAge() {
           return age;
       }
       public void setAge(Integer age) {
           this.age = age;
       }
   }
   ```

    - `@Component` 自动添加 bean 到 spring 容器中。
    - `@ConfigurationProperties` 告诉这个类的属性都是配置文件里的属性，prefix 指定读取配置文件的前缀。

2. 在`build.gradle` 中添加自动生成配置元数据的依赖

   ```groovy
   //配置元数据的依赖
   implementation 'org.springframework.boot:spring-boot-configuration-processor'
   ```

3. 在配置文件中直接使用，值得注意的是：此时设置自定义配置项时会自动提示，且不存在高亮背景

   ```groovy
   # 自定义配置(person)
   person:
     name: 张三
     age: 18
   ```

>   `@ConfigurationProperties`注解使用时需注意：
>
> 1、`@ConfigurationProperties` 配置类的编写必须得是Java语法
>
> 2、若要根据 `@ConfigurationProperties`配置类的方式获取配置文件中配置项的值，则配置类不能使用 `@Data`注解， 属性读写必须得用 `getter` 和 `setter`。

### 属性配置类型

以 `YAML` 文件为例。

- 简单类型

  ```groovy
  server:
    port: 9090
  ```

- List类型

  ```groovy
  person:
    lists: a,b,c,d
  ```

  **注：多个值之间用逗号分割**

- Map类型

  ```groovy
  person:
    maps:
      key1: value1
      key2: value2
  ```

### 属性引用

在`application.yml` 中的各个属性之间，我们也可以直接通过使用占位符的方式来进行引用，如：

1. **属性定义**

   ```groovy
   # 数据库配置信息
   db:
     master:
       username: demo
       password: demo
   ```

2. **属性引用**

   ```groovy
   # 数据库连接信息
   spring:
     datasource:
       driver-class-name: com.mysql.cj.jdbc.Driver
       url: jdbc:mysql://10.201.6.7:3306/demo?serverTimezone=UTC&characterEncoding=utf-8&useSSL=false
       username: ${db.master.username}
       password: ${db.master.password}
   ```

## 三、读取配置文件中的属性

### 1、读取 `application` 文件

**方式1：`@Value`注解**

`@Value` 支持直接从配置文件读取值，同时支持 `SpEL表达式`，即Spring表达式语言，但不支持复杂数据类型(如map)和数据验证

```kotlin
    /**
     * 配置文件中的简单类型：名称
     */
    @Value("\${person.name}")
    val name: String? = null

    /**
     * 支持SpEL表达式， 格式为 #{...}，大括号内为SpEL表达式
     */
    @Value("#{11*4/2}")
    val age: String? = null

    /**
     * 演示：@Value注解读取配置文件中的配置项
     */
    @GetMapping("/readByValue")
    fun readByValue(): Any {
        val sb = StringBuilder()
        sb.append("@Value形式获取配置参数：")
        sb.append(java.lang.String.format("name=%s, age=%s ", name, age))
        return sb.toString()
    }
```

**方式2：配置类 `@ConfigurationProperties`**

虽然使用 `@Value` 注解可以很好的把配置文件中的属性值注入到beans中，但当属性配置文件变多或属性特别多的时候，使用@Value注解将变的很麻烦，这时，我们就可以通过配置类的方式，即 `@ConfigurationProperties` 注解实现属性注入。

依上述 `PersonValue` 配置类为例，通过配置类的方式读取配置文件中的属性值

```kotlin
@Autowired
lateinit var personValue: PersonValue

/**
* 演示：@ConfigurationProperties注解读取配置文件中的配置项
*/
@GetMapping("/readByClass")
fun readByClass(): Any {
    val sb = StringBuilder()
    sb.append("@ConfigurationProperties形式获取配置参数：\n")
    sb.append(
       java.lang.String.format(
           "name=%s, age=%s ",
            personValue.name,
            personValue.age
        )
    )
    return sb.toString()
 }
```

> `@ConfigurationProperties` 和 `@Value`的使用场景：
>
> 只是在某个业务逻辑中获取配置文件的某个值，使用 `@Value`。
>
> 专门编写有一个 Java Bean 来和配置文件映射，使用 `@ConfigurationProperties`。

### 2、读取指定配置文件

随着业务复杂性的增加，配置文件也越来越多，我们会觉得所有的配置都写在一个 properties 文件会使配置显得繁杂不利于管理，因此希望可以把映射属性类的配置单独的抽取出来。由于 Spring Boot 默认读取` application.properties`，因此在抽取之后之前单独的`@ConfigurationProperties(prefix = "person")`已经无法读取到信息。这是可以使用 `@PropertySource` 注解来指定要读取的配置文件。

> **注意**：
>
> - `@PropertySource` 不支持 `YAML` 文件读取，只支持 `Properties` 文件。
> - 使用 `@PropertySource` 加载自定义的配置文件，，由于 `@PropertySource` 指定的文件会优先加载，所以如果在 `applocation.properties `中存在相同的属性配置，则会覆盖前者中对应的值。

**使用步骤**

1. 在`build.gradle` 中添加依赖

   ```groovy
   //配置元数据的依赖
   implementation 'org.springframework.boot:spring-boot-configuration-processor'
   ```

2. `src/main/resources` 目录下新增一个配置文件 `application-source.properties`

3. 创建自定义配置类 `SourceValue.java`

   ```java
   @Component
   @PropertySource(value = "classpath:application-source.properties")
   @ConfigurationProperties(prefix = "com.example")
   public class SourceValue {
   
       /**
        * 用户名
        */
       private String userName;
   
       /**
        * 密码
        */
       private String password;
   
       public String getUserName() {
           return userName;
       }
   
       public void setUserName(String username) {
           this.userName = username;
       }
   
       public String getPassWord() {
           return password;
       }
   
       public void setPassWord(String password) {
           this.password = password;
       }
   }
   ```

4. 在配置文件 `application-source.properties` 中使用自定义配置

   ```groovy
   com.example.user-name = test
   com.example.pass-word = 123456
   ```

5. 获取配置文件中的属性值

   **方式1：`@PropertySource`+`@Value`注解**

   ```kotlin
   /**
    * 用户名
    */
   @Value("\${com.example.user-name}")
   val userName: String? = null
   
   /**
   * 密码
   */
   @Value("\${com.example.pass-word}")
   val passWord: String? = null
   
   /**
   * 演示："@PropertySource + @Value注解读取指定配置文件中的配置项
   */
   @GetMapping("/readBySourceValue")
   fun readBySourceValue(): Any {
       val sb = StringBuilder()
       sb.append("【@PropertySource+@Value】的形式获取指定配置文件的配置参数：")
       sb.append(
           java.lang.String.format(
               "username=%s, password=%s ",
               userName, passWord
           )
       )
       return sb.toString()
   }
   
   ```

   **方式2：`@PropertySource`+`@ConfigurationProperties`注解**

   ```kotlin
   @Autowired
   lateinit var sourceValue: SourceValue
   
   /**
   * 演示："@PropertySource + @ConfigurationProperties注解读取指定配置文件中的配置项
   */
   @GetMapping("/readBySourceClass")
   fun readBySourceClass(): Any {
       val sb = StringBuilder()
       sb.append("【@PropertySource+@ConfigurationProperties】的形式获取指定配置文件的配置参数：\n")
       sb.append(
          java.lang.String.format(
              "userName=%s, passWord=%s ",
               sourceValue.userName,
               sourceValue.passWord
           )
       )
   
       return sb.toString()
   }
   ```

## 四、多环境配置

以 `YAML` 文件为例。`Spring Boot` 项目中多环境配置文件名需要满足`application-{profile}.yml`的格式，其中`{profile}`对应环境标识，默认使用的是`application.yml`。

**使用步骤：**

- **开发环境** `application-dev.yml`

  ```groovy
  # 服务信息
  server:
    port: 9090
  # 数据库连接信息
  spring:
    datasource:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://10.201.6.7:3306/demo?serverTimezone=UTC&characterEncoding=utf-8&useSSL=false
      username: ${db.master.username}
      password: ${db.master.password}
  ```

- **生产环境** `application-prod.yml`

  ```groovy
  # 服务信息
  server:
    port: 8080
  # 数据库连接信息
  spring:
    datasource:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://10.201.6.7:3306/demo?serverTimezone=UTC&characterEncoding=utf-8&useSSL=false
      username: ${db.master.username}
      password: ${db.master.password}
  ```

- **加载运行环境的配置文件**

  ```groovy
  # Spring 配置
  spring:
    # 当前配置
    profiles:
      # 加载开发环境的配置文件内容
      active: dev
      # 使用env环境的配置文件内容替换数据库连接的用户名及密码
      include: env
  ```

- **命令运行jar包，测试配置文件**

  ```groovy
  //可以观察到服务端口被设置为9090，也就是开发环境（dev）
  java -jar xxx.jar
  ```


> **多环境配置思路**
>
> - `application.yml`中配置通用内容，并设置`spring.profiles.active=dev`，以开发环境为默认配置
> - `application-{profile}.yml`中配置各个环境不同的内容
> - 通过命令行方式去激活不同环境的配置

## 五、常见的加载配置顺序

1. 命令行参数运行，所有的配置都可以在命令行上执行，多个配置空格隔开。

   ```
   java -jar springboot-0.0.1-SNAPSHOT.jar --server.port=9999 --sercer.context-path=/spring
   ```

2. 当前应用jar包之外，针对不同`{profile}`环境的`application-{profile}.properties`或`yml`文件配置内容

3. 当前应用jar包之内，针对不同`{profile}`环境的`application-{profile}.properties`或`yml`文件配置内容

4. 当前应用jar包之外的`application.properties`或`yml`文件的配置内容

5. 当前应用jar包之内的`application.properties`和`yml`文件的配置内容

**优先级按上面的顺序由高到低，数字越小优先级越高。优先级高的配置会覆盖优先级低的配置**
