package cn.lttc.redisdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 购物车添加产品Dto
 *
 * @author sunjian
 * @create 2021-04-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncrDto {
    private Integer userId;
    private Integer productId;
}
