package org.example.domain;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private String id;                // 订单号（字符串）
    private Integer userId;           // 用户ID
    private Integer merchantId;       // 商家ID
    private Integer riderId;          // 骑手ID
    private BigDecimal totalAmount;   // 总金额
    private BigDecimal payAmount;     // 实付金额
    private BigDecimal discountAmount;// 优惠金额
    private String address;           // 配送地址
    private String phone;             // 电话
    private String receiver;          // 收件人
    private Integer status;           // 0待支付 1已支付待接单 2已接单 3配送中 4完成 5取消

    private LocalDateTime payTime;    // 支付时间
    private LocalDateTime acceptTime; // 接单时间
    private LocalDateTime pickTime;   // 取餐时间
    private LocalDateTime finishTime; // 完成时间
    private LocalDateTime cancelTime; // 取消时间
    private LocalDateTime createdAt;  // 创建时间
}