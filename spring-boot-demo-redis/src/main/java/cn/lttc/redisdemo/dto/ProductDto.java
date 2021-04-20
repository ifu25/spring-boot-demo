package cn.lttc.redisdemo.dto;

import cn.lttc.redisdemo.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 产品及其对应数量Dto
 *
 * @author sunjian
 * @create 2021-04-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Product product;
    private Integer count;
}
