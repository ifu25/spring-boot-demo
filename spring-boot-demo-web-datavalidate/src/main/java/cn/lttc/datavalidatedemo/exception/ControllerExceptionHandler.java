package cn.lttc.datavalidatedemo.exception;

import cn.lttc.datavalidatedemo.utils.R;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;

/**
 * 处理controller层抛出的异常
 *
 * @author 杨双岭
 * @create 2021-05-07
 */
@RestControllerAdvice(basePackages = "cn.lttc.datavalidatedemo.controller")
public class ControllerExceptionHandler {

    /**
     * 用于捕获DTO抛出的异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        HashMap<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
        });
        return R.error(400,"数据校验出现问题").put("data",errorMap);
    }

    /**
     * 用于捕获直接参数校验抛出的异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public R ConstraintViolationException(ConstraintViolationException e){
        HashMap<String, String> errorMap = new HashMap<>();
        e.getConstraintViolations().forEach(fildError -> {
            PathImpl propertyPath = (PathImpl)fildError.getPropertyPath();
            errorMap.put(propertyPath.getLeafNode().getName(),fildError.getMessage());
        });
        return R.error(400,"数据校验出现问题").put("data",errorMap);
    }

    @ExceptionHandler(value = Exception.class)
    public R handleException(Exception e){
        e.printStackTrace();
        return R.error(500,"系统未处理的异常");
    }

}
