package org.example.domain;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class OrderItem {
    private Integer id;
    private String orderId;
    private Integer productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
}