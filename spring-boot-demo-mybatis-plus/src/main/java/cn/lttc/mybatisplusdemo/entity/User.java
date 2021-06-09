package cn.lttc.mybatisplusdemo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * user实体类
 *
 * @author YangShuangLing
 * @create 2021-04-19
 */
@Data
@TableName("mp_user")
public class User {

    //用户id
    @TableId
    private Long id;

    //姓名
    private String name;

    //年龄
    private Integer age;

    //邮箱
    private String email;
}
