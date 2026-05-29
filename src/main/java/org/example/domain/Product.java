package org.example.domain;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Product {
    private Integer id;
    private Integer merchantId;
    private Integer categoryId;
    private String name;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String image;
    private String description;
    private Integer stock;
    private Integer sales;
    private Integer status;
    private Integer sort;
}