package org.example.mapper;
import org.apache.ibatis.annotations.*;
import org.example.domain.Category;

import java.util.List;
@Mapper
public interface CategoryMapper {
    @Insert("INSERT INTO category(merchant_id,name,sort) VALUES(#{merchantId},#{name},#{sort})")
    int insert(Category category);

    @Select("SELECT * FROM category WHERE merchant_id=#{merchantId} ORDER BY sort")
    List<Category> selectByMerchantId(Integer merchantId);

    @Select("SELECT COUNT(*) FROM category WHERE merchant_id=#{merchantId} AND name=#{name}")
    int countByMerchantAndName(@Param("merchantId") Integer merchantId, @Param("name") String name);

    @Select("SELECT * FROM category WHERE id=#{id}")
    Category selectById(Integer id);

    @Delete("DELETE FROM category WHERE id=#{id}")
    int deleteById(Integer id);
}