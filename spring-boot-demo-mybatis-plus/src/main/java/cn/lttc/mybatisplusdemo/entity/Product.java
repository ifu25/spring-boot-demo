package cn.lttc.mybatisplusdemo.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * product实体类
 * 说明：继承Model，启用AR模式
 *
 * @author YangShuangLing
 * @create 2021-04-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "mp_product")
public class Product extends Model<Product> {

    private static final long serialVersionUID = 1L;

    //商品id
    @TableId(value = "proid")
    private Long proid;

    //商品名称
    private String name;

    //商品副标题
    private String subTitle;

    //产品主图,url相对地址
    private String mainImage;

    //价格,单位-元保留两位小数
    private BigDecimal price;

    //价格,单位-元保留两位小数
    private Integer stock;

    //商品状态.1-在售 0-删除
    @TableLogic
    private Integer status;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 指明AR主键
     * @return
     */
    @Override
    protected Serializable pkVal() {
        return this.proid;
    }

}
