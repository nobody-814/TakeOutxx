package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.domain.Rider;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface RiderMapper {

    // 骑手入驻新增
    @Insert("INSERT INTO rider(user_id, id_card, delivery_scope, status, rating) " +
            "VALUES(#{userId}, #{idCard}, #{deliveryScope}, #{status}, #{rating})")
    int insert(Rider rider);

    // 根据骑手ID查询
    @Select("SELECT * FROM rider WHERE id = #{id}")
    Rider selectById(Integer id);

    // 根据用户ID查询骑手信息（登录用户查自身）
    @Select("SELECT * FROM rider WHERE user_id = #{userId}")
    Rider selectByUserId(Integer userId);

    // 查询在线可接单的骑手（status=1）
    @Select("SELECT * FROM rider WHERE status = 1")
    List<Rider> selectOnlineRider();

    // 修改骑手状态
    @Update("UPDATE rider SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    // 更新骑手评分
    @Update("UPDATE rider SET rating = #{rating} WHERE id = #{id}")
    int updateRating(@Param("id") Integer id, @Param("rating") BigDecimal rating);
}