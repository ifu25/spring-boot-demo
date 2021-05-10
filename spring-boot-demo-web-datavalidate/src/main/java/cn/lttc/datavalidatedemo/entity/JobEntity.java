package cn.lttc.datavalidatedemo.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 工作-演示嵌套校验
 *
 * @author 杨双岭
 * @create 2021-05-10
 */
@Data
public class JobEntity {

    @NotNull(message = "工作id不能为空")
    private Long jobId;

    private String name;

}
