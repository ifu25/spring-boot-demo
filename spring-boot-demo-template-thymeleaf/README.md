# spring-boot-demo-template-thymeleaf

## 一、Thymeleaf 模板引擎介绍

`Thymeleaf` 是现代化服务器端的 Java 模板引擎，不同与其它几种模板的是 Thymeleaf 的语法更加接近 `HTML`，并且具有很高的扩展性。

- 详细资料可以浏览官网：https://www.thymeleaf.org
- [Thymeleaf_3.0.5_中文参考手册.pdf](../docs/file/Thymeleaf_3.0.5_中文参考手册.pdf)

### 特点

- 支持无网络环境下运行，由于它支持 `html` 原型，然后在 `html` 标签里增加额外的属性来达到`模板+数据`的展示方式。 浏览器解释 `html` 时会忽略未定义的标签属性，所以 `Thymeleaf` 的模板可以静态地运行； 当有数据返回到页面时，`Thymeleaf` 标签会动态地替换掉静态内容，使页面动态显示。 所以它可以让前端小姐姐在浏览器中查看页面的静态效果，又可以让程序员小哥哥在服务端查看带数据的动态页面效果。
- 开箱即用，为 `Spring` 提供方言，可直接套用模板实现 `JSTL`、`OGNL` 表达式效果，避免每天因套用模板而修改标签的困扰。 同时开发人员可以扩展自定义的方言。
- SpringBoot官方推荐模板，提供了可选集成模块 `spring-boot-starter-thymeleaf`，可以快速实现表单绑定、属性编辑器、国际化等功能。

## 二、集成步骤

1、引用依赖：

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
}
```

2、编写控制器方法：

```kotlin
/**
 * 循环：用户列表
 */
@RequestMapping("/list")
fun list(map: ModelMap): String {
    map.addAttribute("users", getUserList())
    return "list"
}
```

3、编写模板：`list.html`

模板文件路径 `src/main/resources/templates/list.html`。

注意需要在 `html` 页面中引入 `thymeleaf` 命名空间，否则文件中使用的动态属性 `th` 会提示不存在。

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>用户列表</title>
</head>
<body>

<h1>for 循环</h1>
<div>
    <li th:each="user,iterStat : ${users}">
        <span th:text="${iterStat.index}">index</span>
        <span th:text="${user.userName}">小刚</span>
        <span th:text="${user.age}">18</span>
        <span th:text="${user.deptName}">信息部</span>
    </li>
</div>

</body>
</html>
```

## 三、常用表达式

### ${}

`变量表达式`，用于访问容器上下文环境中的变量。如访问变量 `user` 对象的 `name` 属性：`${user.name}`

### *{}

`选择表达式`，与`变量表达式`有一个重要的区别：`选择表达式`计算的是`选定的对象`，而不是整个环境变量映射。 如果对于单一变量（非对象），则 `${…}` 和 `*{…}` 完全等价。 那什么是`选择的对象呢`？是一个 `th:object` 绑定的对象。

```html

<div th: object="${session.user}">
    <p>姓名: <span th:text="*{name}">张三</span></p>
    <p>年龄: <span th:text="*{age}">李四</span></p>
</div>
```

### #{}

`消息表达式`或`资源表达式`，一般用于做国际化。通常与 `th:text` 属性一起使用，指明声明了 `th:text` 的标签的文本是 `#{}` 中的 `key` 所对应的 `value`，而标签内的文本将不会显示。

```html
<p th:text="#{index.welcome}">这里的内容将不会显示</p>
```

然后新建 `index.properties`：

```properties
index.welcome = 欢迎光临
```

最终网页源码为显示：

```html
<p>欢迎光临</p>
```

### @{}

`链接表达式`，如：

```html
<script th:src="@{/resources/js/jquery/jquery.json-2.4.min.js}"
```

### #maps、#dates、#lists...

`工具对象表达式`，常用于`日期`、`集合`、`数组对象`的访问。这些工具对象就像是 `java` 对象，可以访问对象的方法来进行各种操作。

```
#maps
#dates
#calendars
#numbers
#strings
#objects
#bools
#arrays
#lists
#sets
```

## 四、示例代码

```html
<!--判断-->
<a th:if="${flag == 'yes'}" th:href="https://www.baidu.com">百度</a>
<a th:unless="${flag != 'no'}" th:href="https://www.google.com">google</a>

<div th:switch="${sex}">
    <p th:case="0">男</p>
    <p th:case="1">女</p>
    <p th:case="*">保密</p>
</div>

<!--循环-->
<tr th:each="user,iterStat : ${users}">
    <td th:text="${iterStat.index}">index</td>
    <td th:text="${user.userName}">neo</td>
    <td th:text="${user.age}">6</td>
    <td th:text="${user.deptName}">213</td>
</tr>

<!--定义模板-->
<div th:fragment="nav">
    <a href="/">首页</a>
    <a href="/list">列表循环</a>
    <a href="/condition">条件判断</a>
</div>

<!--引用模板-->
<div th:replace="_nav::nav"></div>
```

## 五、参考文档

https://zhuanlan.zhihu.com/p/183831446

-   [Thymeleaf参考手册（一）：简介](https://zhuanlan.zhihu.com/p/182905976)
-   [Thymeleaf参考手册（二）：标准表达式语法（一）](https://zhuanlan.zhihu.com/p/182910402)
-   [Thymeleaf参考手册（三）：标准表达式语法（二）](https://zhuanlan.zhihu.com/p/182942361)
-   [Thymeleaf参考手册（四）：标准表达式语法（三）](https://zhuanlan.zhihu.com/p/182950014)
-   [Thymeleaf参考手册（五）：设置属性值](https://zhuanlan.zhihu.com/p/182969114)
-   [Thymeleaf参考手册（六）：迭代](https://zhuanlan.zhihu.com/p/183699043)
-   [Thymeleaf参考手册（七）：条件评估](https://zhuanlan.zhihu.com/p/183701442)
-   [Thymeleaf参考手册（八）：模板布局](https://zhuanlan.zhihu.com/p/183703071)
-   [Thymeleaf参考手册（九）：局部变量](https://zhuanlan.zhihu.com/p/183715854)
-   [Thymeleaf参考手册（十）：优先级](https://zhuanlan.zhihu.com/p/183718086)
-   [Thymeleaf参考手册（十一）：注释和块](https://zhuanlan.zhihu.com/p/183815589)
-   [Thymeleaf参考手册（十二）：内联](https://zhuanlan.zhihu.com/p/183817673)
-   [Thymeleaf参考手册（十三）：文字模板模式](https://zhuanlan.zhihu.com/p/183821971)
-   [Thymeleaf参考手册（十四）：其它配置](https://zhuanlan.zhihu.com/p/183825979)
-   [Thymeleaf参考手册（十五）：模板缓存](https://zhuanlan.zhihu.com/p/183828956)
-   [Thymeleaf参考手册（十六）：模板解耦逻辑](https://zhuanlan.zhihu.com/p/183829522)
