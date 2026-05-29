package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.domain.Order;
import java.util.List;

@Mapper
public interface OrderMapper {

    // 创建订单
    @Insert("INSERT INTO `order`(id, user_id, merchant_id, rider_id, total_amount, pay_amount, " +
            "discount_amount, address, phone, receiver, status, created_at) " +
            "VALUES(#{id}, #{userId}, #{merchantId}, #{riderId}, #{totalAmount}, #{payAmount}, " +
            "#{discountAmount}, #{address}, #{phone}, #{receiver}, #{status}, #{createdAt})")
    int insert(Order order);

    // 根据订单ID查询
    @Select("SELECT * FROM `order` WHERE id = #{id}")
    Order selectById(String id);

    // 查询我的订单
    @Select("SELECT * FROM `order` WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> selectByUserId(Integer userId);

    // 查询商家收到的订单
    @Select("SELECT * FROM `order` WHERE merchant_id = #{merchantId} ORDER BY created_at DESC")
    List<Order> selectByMerchantId(Integer merchantId);

    // 查询骑手可接订单
    @Select("SELECT * FROM `order` WHERE status = 1")
    List<Order> selectWaitRider();

    // 修改订单状态
    @Update("UPDATE `order` SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") String id, @Param("status") Integer status);

    // 绑定骑手
    @Update("UPDATE `order` SET rider_id = #{riderId} WHERE id = #{id}")
    int bindRider(@Param("id") String id, @Param("riderId") Integer riderId);
}