package cn.lttc.datavalidatedemo.entity;

import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 品牌-演示基本使用
 *
 * @author 杨双岭
 * @create 2021-05-06
 */
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
