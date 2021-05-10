package cn.lttc.datavalidatedemo.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自定义校验注解-用于校验值必须是0或1
 *
 * @author 杨双岭
 * @create 2021-05-07
 */
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
