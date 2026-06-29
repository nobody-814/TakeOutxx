package org.example.domain;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Review {
    private Integer id;
    private String orderId;
    private Integer userId;
    private Integer merchantId;
    private Integer riderId;
    private Integer productId;
    private BigDecimal rating;
    private String content;
    private Integer type;        // 1-商家 2-商品 3-骑手
    private LocalDateTime createdAt;
}