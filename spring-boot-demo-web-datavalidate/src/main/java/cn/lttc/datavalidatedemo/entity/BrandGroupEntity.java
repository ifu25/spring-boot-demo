package cn.lttc.datavalidatedemo.entity;

import cn.lttc.datavalidatedemo.valid.Group;
import cn.lttc.datavalidatedemo.valid.ListValue;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌-演示分组校验
 *
 * @author 杨双岭
 * @create 2021-05-07
 */
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
    @ListValue(vals={0,1},groups = {Group.Add.class, Group.Update.class})
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
