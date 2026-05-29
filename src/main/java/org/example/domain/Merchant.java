package org.example.domain;

import lombok.Data;

@Data
public class Merchant  {

    private Integer id; // 商家ID

    private Integer userId; // 关联User表的商家用户ID（role=1）

    private String shopName; // 店铺名称

    private String address; // 店铺地址（用于配送范围）

    private String businessHours; // 营业时间

    private String avatar; // 店铺头像

    private Double rating; // 店铺评分（0-5分）

    private Integer status; // 店铺状态：0-未营业，1-营业中，2-休息
}