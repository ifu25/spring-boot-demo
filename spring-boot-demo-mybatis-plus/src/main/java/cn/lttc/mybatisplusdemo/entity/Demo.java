package cn.lttc.mybatisplusdemo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author yangshuangling
 * @since 2021-04-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mp_demo")
public class Demo extends Model<Demo> {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private Integer age;

    private String email;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
