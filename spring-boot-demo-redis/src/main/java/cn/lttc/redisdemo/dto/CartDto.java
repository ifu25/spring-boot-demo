package cn.lttc.redisdemo.dto;

import cn.lttc.redisdemo.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 购物车Dto
 *
 * @author sunjian
 * @create 2021-04-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Integer userId;
    private List<ProductDto> productDto;
}
