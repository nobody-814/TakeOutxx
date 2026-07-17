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

    @Update("UPDATE `order` SET status = #{status} WHERE id = #{id} AND status = #{expected}")
    int updateStatus(@Param("id") String id, @Param("status") Integer status, @Param("expected") Integer expected);

    // 支付：待支付(0) → 待接单(1)，记录支付时间
    @Update("UPDATE `order` SET status = 1, pay_time = NOW() WHERE id = #{id} AND status = 0")
    int payOrder(@Param("id") String id);

    // 骑手抢单：待接单(1) → 配送中(3)，原子绑定骑手
    @Update("UPDATE `order` SET rider_id = #{riderId}, status = 3, accept_time = NOW() WHERE id = #{id} AND rider_id IS NULL AND status = 1")
    int riderTakeOrder(@Param("id") String id, @Param("riderId") Integer riderId);

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

    // 取消：待支付(0) 或 待接单(1) → 已取消(5)
    @Update("UPDATE `order` SET status = 5, cancel_time = NOW() WHERE id = #{id} AND status IN (0, 1)")
    int cancelOrder(@Param("id") String id);

    // 完成配送：配送中(3) → 已完成(4)
    @Update("UPDATE `order` SET status = 4, finish_time = NOW() WHERE id = #{id} AND status = 3")
    int completeOrder(@Param("id") String id);
}