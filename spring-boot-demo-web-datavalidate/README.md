# spring-boot-demo-web-datavalidate

## 简介

>  本demo使用JSR-303规范进行数据校验。

​        在任何时候，当你要处理一个应用程序的业务逻辑，数据校验是必须要考虑和面对的事情。应用程序必须通过某种手段来确保输入进来的数据从语义上来讲是正确的。在通常的情况下，应用程序是分层的，不同的层由不同的开发人员来完成。很多时候同样的数据验证逻辑会出现在不同的层，这样就会导致代码冗余和一些管理的问题，比如说语义的一致性等。为了避免这样的情况发生，最好是将验证逻辑与相应的域模型进行绑定。

​        JSR-303（Java Specification Requests Java规范要求）是 JAVA EE 6 中的一项子规范，叫做 Bean Validation。Bean Validation 为 JavaBean 验证定义了相应的元数据模型和 API。在应用程序中，通过使用 Bean Validation 或是你自己定义的 constraint，例如  @NotNull, @Max, @ZipCode， 就可以确保数据模型（JavaBean）的正确性。constraint  可以附加到字段，getter 方法，类或者接口上面。对于一些特定的需求，用户可以很容易的开发定制化的 constraint。Bean  Validation 是一个运行时的数据验证框架，在验证之后验证的错误信息会被马上返回。

> Bean Validation 规范内嵌的约束注解

| 注解                      | 功能                                                         |
| ------------------------- | ------------------------------------------------------------ |
| @Notnull                  | 验证对象是否不为null，无法检查长度为0的字符串，用于校验基本数据类型包装类 |
| @Null                     | 验证对象是否为null                                           |
| @AssertTrue               | 验证Boolean对象是否为true                                    |
| @AssertFalse              | 验证Boolean对象是否为false                                   |
| @Max(value)               | 验证Number和String对象是否小于等于指定的值                   |
| @Min(value)               | 验证Number和String对象是否大于等于指定的值                   |
| @DecimalMax(value)        | 被标注的值必须不大于约束中指定的最大值。这个约束的参数是一个通过BigDecimal定义的最大值的字符串表示，小数存在精度 |
| @DecimalMin(value)        | 被标注的值必须不大于约束中指定的最小值。这个约束的参数是一个通过BigDecimal定义的最小值的字符串表示，小数存在精度 |
| @Digits(integer,fraction) | 验证字符串是否是符合指定格式的数组，integer指定整数精度，fraction指定小数精度 |
| @Size(min,max)            | 验证对象(Array/Collection/Map/String)长度是否在给定的范围之内 |
| @Past                     | 验证Date和Calendar对象是否是否在当前时间之前                 |
| @Future                   | 验证Date和Calendar对象是否是否在当前时间之后                 |
| @Pattern                  | 验证String对象是否符合正则表达式的规则                       |
| @NotBlank                 | 检查约束字段是不是Null，被Trim的长度是否大于0。只对字符串有效 |
| @URL                      | 验证是否是合法的url                                          |
| @Email                    | 验证是否是合法的邮件地址                                     |
| @CreditcardNumber         | 验证是否是合法的信用卡号码                                   |
| @Length(min,max)          | 验证字符串的长度必须在指定的范围内                           |
| @NotEmpty                 | 检查元素是否为NULL或者EMPTY。用于Array/Collection/Map/String |
| @Range(min,max,message)   | 验证属性值必须在合适的范围内                                 |

- @NotBlank：用于校验字符串，至少含有一个字符

- @NotNull：用于校验基本数据类型包装类，不为null

- @NotEmpty：用于Array、Collection、Map

  > 注：以上三个 constraint要求参数必须携带，其他不要求

## 实例

### 基本应用

#### 	引入依赖

springboot2.3.0之后的版本不再整合校验包，所以需要自己引入依赖

````groovy
implementation 'org.springframework.boot:spring-boot-starter-validation:2.4.3'
````

#### 	添加校验注解，编写message提示

> 以brand为例：添加品牌实体类，要求前端发送的请求数据满足

 	1. 品牌名：不为空
 	2. logo：必须为合法url资源
 	3. 检索首字母：不为空且必须是一个字母
 	4. 排序：不为空且必须为大于等于0的正整数

```java
@Data
public class BrandEntity implements Serializable {
   private static final long serialVersionUID = 1L;

   /**
    * 品牌id
    */
   private Long brandId;
    
   /**
    * 品牌名
    */
   @NotBlank(message = "品牌名至少填写一个字符")
   private String name;
    
   /**
    * 品牌logo地址
    */
   @URL(message = "logo必须是一个合法的url地址")
   private String logo;
    
   /** 
    * 介绍
    */
   private String descript;
    
   /**
    * 显示状态[0-不显示；1-显示]
    */
   private Integer showStatus;
    
   /**
    * 检索首字母
    */
   @NotBlank(message = "检索首字母不能为空")
   @Pattern(regexp = "^[a-zA-Z]$",message = "检索首字母必须是一个字母")
   private String firstLetter;
    
   /**
    * 排序
    */
   @NotNull(message = "排序不能为空")
   @Min(value = 0, message = "排序必须为大于等于0的正整数")
   private Integer sort;
    
}
```

#### 		开启校验功能

1. 默认响应

   在需要校验的bean前添加@Valid，校验不通过，返回400

```
    @PostMapping("/save1")
    public R save1(@Valid @RequestBody BrandEntity brand){
        //处理逻辑....
        return R.ok();
    }
```

​		响应：

```json
{
    "timestamp": "2021-05-10T07:56:47.835+00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "",
    "path": "/brand/save1"
}
```

​		这样的返回结果往往不是我们想要的，所以请看2

2. 在本方法中处理返回结果

   （注意使用本方法处理，该方法的类不能用@Validated、@Valid修饰）

   ```
   @PostMapping("/save2")
   public R save2(@Valid @RequestBody BrandEntity brand, BindingResult result){
       if (result.hasErrors()){
           HashMap<Object, Object> map = new HashMap<>();
           //1.获取校验的错误结果
           result.getFieldErrors().forEach((item)->{
               //2.获取到错误提示
               String message = item.getDefaultMessage();
               //获取错误的属性名字
               String field = item.getField();
               //3.封装结果集
               map.put(field,message);
           });
           return R.error(400,"提交的数据不合法").put("data",map);
       }else {
           //处理逻辑....
       }
       return R.ok();
   }
   ```

   如果每个方法都需要校验，那么每个方法都要单独封装结果太麻烦了，所以请看3

3. 定义统一异常处理

   > 创建exception包，用于处理程序抛出的异常

   > 创建异常处理类

   ```java
   @RestControllerAdvice(basePackages = "cn.lttc.datavalidatedemo.controller")
   public class ControllerExceptionHandler {
   
       @ExceptionHandler(value = MethodArgumentNotValidException.class)
       public R handleVaildException(MethodArgumentNotValidException e){
           BindingResult bindingResult = e.getBindingResult();
           HashMap<String, String> errorMap = new HashMap<>();
           bindingResult.getFieldErrors().forEach(fieldError -> {
               errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
           });
           return R.error(400,"数据校验出现问题").put("data",errorMap);
       }
   
   }
   ```

   - 说明：当controller方法中没有**BindingResult**来捕获异常时，异常就会抛出，由**ControllerAdvice**注解捕获；**basePackages**用来标注处理哪个包的异常；**@ExceptionHandler**用来标识该方法捕获哪种异常；**MethodArgumentNotValidException**为校验不通过时抛出的异常

### 分组校验

> 假设：品牌在新增和修改时，需要校验的数据是不同的

1. 品牌id：由于品牌id是自增的，所以在新增时不能携带品牌id；修改时必须携带品牌id
2. 品牌名：无论新增还是修改，都必须携带品牌名且不为空
3. logo地址：新增时不为空，修改时可以为空但必须是合法URL
4. 检索首字母：新增时不为空，修改时可以为空但必须是一个字母
5. 排序：无论新增还是修改，都不能为空，且必须大于等于0

> 步骤：

1. 编写group分组（接口）
2. 在校验注解中使用group属性，指明该校验在哪个组生效
3. 在方法中使用@Validated注解，指明使用哪个分组

- 分组类

```java
public class Group {

    public interface Add{}
    public interface Update{}

}
```

- 实体类

```java
@Data
public class BrandGroupEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @Null(message = "新增不能指定id",groups = {Group.Add.class})
    @NotNull(message = "必须指定品牌id",groups = {Group.Update.class})
    private Long brandId;

    /**
     * 品牌名
     */
    @NotBlank(message = "品牌名至少填写一个字符",groups = {Group.Update.class,Group.Add.class})
    private String name;

    /**
     * 品牌logo地址
     */
    @NotBlank(message = "必须指定品牌logo",groups = {Group.Add.class})
    @URL(message = "logo必须是一个合法的url地址",groups = {Group.Add.class,Group.Update.class})
    private String logo;

    /**
     * 介绍
     */
    private String descript;

    /**
     * 显示状态[0-不显示；1-显示]
     */
    private Integer showStatus;

    /**
     * 检索首字母
     */
    @NotBlank(message = "检索首字母不能为空",groups = {Group.Add.class})
    @Pattern(regexp = "^[a-zA-Z]$",message = "检索首字母必须是一个字母",groups = {Group.Add.class,Group.Update.class})
    private String firstLetter;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空",groups = {Group.Add.class,Group.Update.class})
    @Min(value = 0, message = "排序必须为大于等于0的正整数",groups = {Group.Add.class,Group.Update.class})
    private Integer sort;

}
```

- controller层

  使用@Validated注解，来开启校验和标注使用哪个校验分组

  ```
  @PostMapping("/save4")
  public R save4(@Validated({Group.Add.class}) @RequestBody BrandGroupEntity brand){
      return R.ok();
  }
  ```

  >  注意：默认没有指定分组校验的注解（如@NotBlank)，在分组校验情况@Validated下**不生效**；
  >
  > ​			controller中指定使用分组校验的参数，未标注分组校验的注解**不生效**；
  >
  > ​			分组校验可用来完成多场景的复杂校验。

### 自定义校验

> 假设：品牌bean中，显示状态必须是0或1

（虽然用正则可以实现，但是有些时候光正则不能完成需要的校验功能，考虑使用自定义校验）

> 步骤：

1. 编写一个自定义的校验注解

   ```java
   @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
   @Retention(RUNTIME)
   @Documented
   @Constraint(validatedBy = {ListValueConstraintValidator.class})  //指定用什么校验器
   public @interface ListValue {
   
       //JSR-303中强制添加的三个属性
   
       //默认错误信息从哪取
       String message() default "{cn.lttc.datavalidatedemo.valid.ListValue.message}";
   
       //可以指定属性分组
       Class<?>[] groups() default { };
   
       //可以指定严重级别
       Class<? extends Payload>[] payload() default { };
   
       //设置标准属性值
       int[] vals() default { };
   }
   ```

2. 编写一个自定义的校验器

   ```java
   public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {
   
       private Set<Integer> set = new HashSet<>();
   
       /**
        * 初始化方法
        * @param constraintAnnotation ListValue标准值
        */
       @Override
       public void initialize(ListValue constraintAnnotation) {
   
           int[] vals = constraintAnnotation.vals();
           for (int val : vals){
               set.add(val);
           }
       }
   
       /**
        * 编写校验逻辑
        * @param value 接收的值
        * @param context
        * @return 是否通过
        */
       @Override
       public boolean isValid(Integer value, ConstraintValidatorContext context) {
           return set.contains(value);
       }
   }
   ```

3. 关联自定义校验器和自定义注解

   在自定义校验器中关联注解：

   ```
   public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> //指定用什么校验注解
   ```

   在自定义注解中关联校验器：

   ```
   @Constraint(validatedBy = {ListValueConstraintValidator.class})  //指定用什么校验器
   ```

4. 使用

   ```
   /**
    * 显示状态[0-不显示；1-显示]
    */
   @ListValue(vals={0,1},groups = {Group.Add.class, Group.Update.class})
   private Integer showStatus;
   ```

### 直接参数校验

   	有时候接口的参数比较少，只有一个或者两个参数，这时候就没必要定义一个DTO来接收参数，可以直接接收参数，在controller中校验。这时controller类需要用@Validated注解标识。

> 示例：传入的id不为空

````java
@Validated
@RestController
@RequestMapping("brand")
public class BrandController {
    @GetMapping("/get")
    public R getbrand(@NotNull(message = "Id不能为空") Long id){
        return R.ok();
    }
}    
````

直接参数校验抛出的异常为：ConstraintViolationException

```
@ExceptionHandler(value = ConstraintViolationException.class)
public R ConstraintViolationException(ConstraintViolationException e){
    HashMap<String, String> errorMap = new HashMap<>();
    e.getConstraintViolations().forEach(fildError -> {
        PathImpl propertyPath = (PathImpl)fildError.getPropertyPath();
        errorMap.put(propertyPath.getLeafNode().getName(),fildError.getMessage());
    });
    return R.error(400,"数据校验出现问题").put("data",errorMap);
}
```

### 嵌套校验

​	前面的示例中，DTO类里面的字段都是基本数据类型和String等类型。

​	但是实际场景中，有可能某个字段也是一个对象，如果我们需要对这个对象里面的数据也进行校验，可以使用嵌套校验。

​	假如User中还用一个Job对象, 比如下面的结构。需要注意的是，在job类的校验上面一定要加上@Valid注解。

> 示例：在传入User的时候，要同时校验Jod

UserEntity：

```java
@Data
public class UserEntity {

    @NotNull(message = "用户id不能为空")
    private Long userId;

    private String name;

    @Valid
    @NotNull(message = "用户工作不能为空")
    private JobEntity job;

    @Valid
    private List<JobEntity> jobs;
}
```

JobEntity:

```
@Data
public class JobEntity {

    @NotNull(message = "工作id不能为空")
    private Long jobId;

    private String name;

}
```

controller：

```
@PostMapping("/save5")
public R save5(@Valid @RequestBody UserEntity user){
    return R.ok();
}
```

### @Validated和@Valid的区别联系

首先，@Validated和@Valid都能实现基本的验证功能，也就是如果你是想验证一个参数是否为空，长度是否满足要求这些简单功能，使用哪个注解都可以。

但是这两个注解在分组、注解作用的地方、嵌套验证等功能上两个有所不同。下面列下这两个注解主要的不同点。

- @Valid注解是JSR303规范的注解，@Validated注解是Spring框架自带的注解；

- @Valid不具有分组校验功能，@Validated具有分组校验功能；

- @Valid可以用在方法、构造函数、方法参数和成员属性（字段）上,@Validated可以用在类型、方法和方法参数上。

- @Valid加在成员属性上可以对成员属性进行嵌套验证，而@Validate不能加在成员属性上，所以不具备这个功能。

- @Valid不能加在类上，不具备直接参数校验功能，而@Validate加在类上可以对简单参数进行直接校验。

> 作用范围区别
| 作用范围 | @Validated | @Valid |
| :----: | :----: | :----: |
| 类 | √（支持直接参数校验） | × |
| 方法 | √ | √ |
| 构造函数 | × | √ |
| 方法参数 | √（支持分组校验） | √ |
| 成员属性 | × | √（支持嵌套校验） |

> 应用场景区别
| 应用场景 | @Validated | @Valid |
| :----: | :----: | :----: |
| 注解来源 | Spring框架自带 | JSR303规范 |
| 作用于方法参数上，支持普通bean校验 | √ | √ |
| 作用于方法参数上，支持分组校验 | √ | × |
| 作用于类上，支持直接参数校验 | √ | × |
| 作用于成员属性上，支持嵌套校验 | × | √ |



