package org.example.mapper;


import org.apache.ibatis.annotations.*;
import org.example.domain.Merchant;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface MerchantMapper {
    //新增商家
    @Insert("INSERT INTO merchant(id,user_id,shop_name,address,business_hours,avatar,rating,status)"+
            "VALUES(#{id},#{userId},#{shopName},#{address},#{businessHours},#{avatar},#{rating},#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Merchant merchant);

    @Select("SELECT * FROM merchant WHERE id=#{id} AND status !='3'")
    Merchant selectById(long id);

    @Select("SELECT *FROM merchant WHERE user_id=#{userId} AND status !='3'")
    Merchant selectByUserId(long userId);

    @Select("SELECT * FROM merchant WHERE status != '3'")
    List<Merchant> selectAll();

    // 更新商家状态（如营业状态）
    @Update("UPDATE merchant SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    //修改商家的基本信息
    @Update("UPDATE merchant SET shop_name = #{shopName}, address = #{address}, business_hours = #{businessHours}, " +
            "avatar = #{avatar}, rating = #{rating} WHERE id = #{id}")
    int updateMerchantInfo(Merchant merchant);
    //按状态检索商家
    @Select("SELECT * FROM merchant WHERE status = #{status} AND status != '3'")
    List<Merchant> selectByStatus(Integer status);
    //按名称模糊查询
    @Select("SELECT * FROM merchant WHERE shop_name LIKE CONCAT('%', #{keyword}, '%') AND status != '3'")
    List<Merchant> searchByShopName(String keyword);
    //分页查询商家
    @Select("SELECT * FROM merchant WHERE status != '3' LIMIT #{offset}, #{pageSize}")
    List<Merchant> selectByPage(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    //统计商家数量
    @Select("SELECT COUNT(*) FROM merchant WHERE status != '3'")
    int countTotalMerchants();

    // 按状态统计数量
    @Select("SELECT COUNT(*) FROM merchant WHERE status = #{status} AND status != '3'")
    int countByStatus(Integer status);
    //商家评分更新
    @Update("UPDATE merchant SET rating = #{newRating} WHERE id = #{id}")
    int updateRating(@Param("id") Long id, @Param("newRating") Double newRating);
    //封禁商家
    @Update("UPDATE merchant SET status = 3 WHERE id = #{id}")
    int banMerchant(Long id);
    









    














}
