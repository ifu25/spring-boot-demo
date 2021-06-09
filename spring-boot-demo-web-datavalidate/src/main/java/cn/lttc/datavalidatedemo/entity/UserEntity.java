package cn.lttc.datavalidatedemo.entity;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户-演示嵌套校验
 *
 * @author 杨双岭
 * @create 2021-05-10
 */
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
