## 新建 Demo 项目方法

> 2021-03-25 @xinggang

1. 拷贝 `spring-boot-demo-hello` 项目

2. 将新项目添加到根项目的 `settings.gradle` 中

   ```
   include 'spring-boot-demo-swagger'
   ```

3. 同步 `Gradle`

4. 修改新项目`包名`、`入口类名`等

5. 启动项目，测试成功后，开始在新项目中引用依赖并编写代码。依赖在新项目的 `build.gralde `中配置。

6. 编写 `README.md` 说明文档

7. 🎉完成~
