package cn.lttc.datavalidatedemo.controller;

import cn.lttc.datavalidatedemo.entity.BrandEntity;
import cn.lttc.datavalidatedemo.entity.BrandGroupEntity;
import cn.lttc.datavalidatedemo.entity.UserEntity;
import cn.lttc.datavalidatedemo.utils.R;
import cn.lttc.datavalidatedemo.valid.Group;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;

/**
 * 控制层
 *
 * @author 杨双岭
 * @create 2021-05-06
 */
@Validated
@RestController
@RequestMapping("brand")
public class BrandController {

    //region ========== 1.演示默认响应 ==========
    @PostMapping("/save1")
    public R save1(@Valid @RequestBody BrandEntity brand){
        //处理逻辑....
        return R.ok();
    }
    //endregion
    
    //region ========== 2.演示在本方法封装结果集 ==========
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
    //endregion

    //region ========== 3.演示在统一异常处理类中处理 ==========
    @PostMapping("/save3")
    public R save3(@Valid @RequestBody BrandEntity brand){
        //处理逻辑....
        return R.ok();
    }
    //endregion
    
    //region ========== 4.演示分组校验 ==========
    @PostMapping("/save4")
    public R save4(@Validated({Group.Add.class}) @RequestBody BrandGroupEntity brand){
        return R.ok();
    }
    //endregion

    //region ========== 5.演示直接参数校验 ==========
    @GetMapping("/get")
    public R getbrand(@NotNull(message = "Id不能为空") Long id){
        return R.ok();
    }
    //endregion
    
    //region ========== 6.演示嵌套校验 ==========
    @PostMapping("/save5")
    public R save5(@Valid @RequestBody UserEntity user){
        return R.ok();
    }
    //endregion
}
