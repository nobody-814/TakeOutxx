package org.example.domain;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class Rider {
    private Integer id;
    private Integer userId;
    private String idCard;
    private String deliveryScope;
    private Integer status;
    private BigDecimal rating;
}