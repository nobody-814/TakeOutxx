package org.example.domain;

import lombok.Data;

@Data
public class Cart {
    private Integer id;
    private Integer userId;
    private Integer merchantId;
    private Integer productId;
    private Integer quantity;

    // 非数据库字段，用于关联查询商品
    private Product product;
}