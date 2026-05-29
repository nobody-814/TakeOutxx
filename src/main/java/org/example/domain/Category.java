package org.example.domain;
import lombok.Data;
@Data
public class Category {
    private Integer id;
    private Integer merchantId;
    private String name;
    private Integer sort;
}