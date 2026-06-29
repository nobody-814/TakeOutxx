package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.domain.Order;
import java.util.List;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO `order`(id, user_id, merchant_id, rider_id, total_amount, pay_amount, " +
            "discount_amount, address, phone, receiver, status, created_at) " +
            "VALUES(#{id}, #{userId}, #{merchantId}, #{riderId}, #{totalAmount}, #{payAmount}, " +
            "#{discountAmount}, #{address}, #{phone}, #{receiver}, #{status}, #{createdAt})")
    int insert(Order order);

    @Select("SELECT * FROM `order` WHERE id = #{id}")
    Order selectById(String id);

    @Select("<script>" +
            "SELECT * FROM `order` WHERE user_id = #{userId}" +
            "<if test='status != null'> AND status = #{status}</if>" +
            " ORDER BY created_at DESC" +
            "</script>")
    List<Order> selectByUserId(@Param("userId") Integer userId, @Param("status") Integer status);

    @Select("<script>" +
            "SELECT * FROM `order` WHERE merchant_id = #{merchantId}" +
            "<if test='status != null'> AND status = #{status}</if>" +
            " ORDER BY created_at DESC" +
            "</script>")
    List<Order> selectByMerchantId(@Param("merchantId") Integer merchantId, @Param("status") Integer status);

    @Select("SELECT * FROM `order` WHERE status = 1")
    List<Order> selectWaitRider();

    @Update("UPDATE `order` SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") String id, @Param("status") Integer status);

    @Update("UPDATE `order` SET rider_id = #{riderId} WHERE id = #{id}")
    int bindRider(@Param("id") String id, @Param("riderId") Integer riderId);

    @Select("SELECT * FROM `order` WHERE rider_id = #{riderId} AND status = 3")
    List<Order> selectCurrentByRiderId(Integer riderId);

    @Select("<script>" +
            "SELECT * FROM `order` WHERE rider_id = #{riderId}" +
            "<if test='status != null'> AND status = #{status}</if>" +
            " ORDER BY created_at DESC" +
            "</script>")
    List<Order> selectByRiderId(@Param("riderId") Integer riderId, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM `order` WHERE merchant_id = #{merchantId} AND status >= 1 AND status != 5")
    int countByMerchantId(Integer merchantId);

    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM `order` WHERE merchant_id = #{merchantId} AND status >= 1 AND status != 5")
    java.math.BigDecimal sumSalesByMerchantId(Integer merchantId);

    @Update("UPDATE `order` SET status = 5, cancel_time = NOW() WHERE id = #{id}")
    int cancelOrder(@Param("id") String id);
}