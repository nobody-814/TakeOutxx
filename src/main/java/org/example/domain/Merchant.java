package org.example.domain;

import lombok.Data;

@Data
public class Merchant {

    private Integer id;
    private Integer userId;
    private String shopName;
    private String address;
    private String businessHours;
    private String avatar;
    private Double rating;
    private Integer reviewCount;
    private Integer status;
}