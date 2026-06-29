package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.domain.Merchant;
import java.util.List;

@Mapper
public interface MerchantMapper {

    @Insert("INSERT INTO merchant(user_id, shop_name, address, business_hours, avatar, rating, review_count, status) " +
            "VALUES(#{userId}, #{shopName}, #{address}, #{businessHours}, #{avatar}, 5.0, 0, 0)")
    int insert(Merchant merchant);

    @Select("SELECT * FROM merchant WHERE id = #{id}")
    Merchant selectById(long id);

    @Select("SELECT * FROM merchant WHERE user_id = #{userId}")
    Merchant selectByUserId(long userId);

    @Select("SELECT * FROM merchant WHERE status != 3")
    List<Merchant> selectValid();

    @Update("UPDATE merchant SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") long id, @Param("status") int status);

    @Update("UPDATE merchant SET shop_name = #{shopName}, address = #{address}, " +
            "business_hours = #{businessHours}, avatar = #{avatar} WHERE id = #{id}")
    int updateMerchantInfo(Merchant merchant);

    @Update("UPDATE merchant SET rating = #{rating} WHERE id = #{id}")
    int updateRating(@Param("id") long id, @Param("rating") double rating);

    @Select("SELECT * FROM merchant WHERE shop_name LIKE CONCAT('%',#{keyword},'%') AND status != 3")
    List<Merchant> searchByShopName(String keyword);

    @Update("UPDATE merchant SET status = 3 WHERE id = #{id}")
    int banMerchant(long id);

    @Select("SELECT * FROM merchant WHERE status != 3 LIMIT #{offset}, #{pageSize}")
    List<Merchant> selectByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Update("UPDATE merchant SET rating = #{rating} WHERE id = #{id}")
    int updateRatingOnly(@Param("id") Integer id, @Param("rating") Double rating);

    @Update("UPDATE merchant SET review_count = review_count + 1 WHERE id = #{id}")
    int incrementReviewCount(@Param("id") Integer id);
}
