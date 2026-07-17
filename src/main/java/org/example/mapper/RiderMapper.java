package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.domain.Rider;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface RiderMapper {

    @Insert("INSERT INTO rider(user_id, id_card, delivery_scope, status, rating, review_count) " +
            "VALUES(#{userId}, #{idCard}, #{deliveryScope}, 0, 5.0, 0)")
    int insert(Rider rider);

    @Select("SELECT * FROM rider WHERE id = #{id}")
    Rider selectById(Integer id);

    @Select("SELECT * FROM rider WHERE user_id = #{userId}")
    Rider selectByUserId(Integer userId);

    @Select("SELECT * FROM rider WHERE status = 1")
    List<Rider> selectOnlineRider();

    @Update("UPDATE rider SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    @Update("UPDATE rider SET rating = #{rating} WHERE id = #{id}")
    int updateRating(@Param("id") Integer id, @Param("rating") BigDecimal rating);

    @Update("UPDATE rider SET rating = (SELECT ROUND(AVG(rating), 1) FROM review WHERE rider_id = #{id} AND type = 3), review_count = (SELECT COUNT(*) FROM review WHERE rider_id = #{id} AND type = 3) WHERE id = #{id}")
    int refreshRatingAndCount(@Param("id") Integer id);
}