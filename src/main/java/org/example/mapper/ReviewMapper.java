package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.domain.Review;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ReviewMapper {

    @Insert("INSERT INTO review(order_id, user_id, merchant_id, rider_id, product_id, rating, content, type, created_at) " +
            "VALUES(#{orderId}, #{userId}, #{merchantId}, #{riderId}, #{productId}, #{rating}, #{content}, #{type}, NOW())")
    int insert(Review review);

    @Select("SELECT * FROM review WHERE merchant_id = #{merchantId} AND type = 1 ORDER BY created_at DESC")
    List<Review> selectByMerchantId(Integer merchantId);

    @Select("SELECT * FROM review WHERE product_id = #{productId} AND type = 2 ORDER BY created_at DESC")
    List<Review> selectByProductId(Integer productId);

    @Select("SELECT * FROM review WHERE rider_id = #{riderId} AND type = 3 ORDER BY created_at DESC")
    List<Review> selectByRiderId(Integer riderId);

    @Select("SELECT * FROM review WHERE order_id = #{orderId}")
    List<Review> selectByOrderId(String orderId);

    @Select("SELECT COUNT(*) FROM review WHERE order_id = #{orderId}")
    int countByOrderId(String orderId);

    @Select("SELECT COALESCE(AVG(rating), 5.0) FROM review WHERE merchant_id = #{merchantId} AND type = 1")
    BigDecimal avgMerchantRating(Integer merchantId);

    @Select("SELECT COALESCE(AVG(rating), 5.0) FROM review WHERE product_id = #{productId} AND type = 2")
    BigDecimal avgProductRating(Integer productId);

    @Select("SELECT COALESCE(AVG(rating), 5.0) FROM review WHERE rider_id = #{riderId} AND type = 3")
    BigDecimal avgRiderRating(Integer riderId);
}
