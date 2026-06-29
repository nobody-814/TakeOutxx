package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.domain.Product;
import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ProductMapper {

    @Insert("INSERT INTO product(merchant_id, category_id, name, price, original_price, " +
            "image, description, stock, sales, status, sort, rating, review_count) " +
            "VALUES(#{merchantId}, #{categoryId}, #{name}, #{price}, #{originalPrice}, " +
            "#{image}, #{description}, #{stock}, #{sales}, #{status}, #{sort}, 5.0, 0)")
    int insert(Product product);

    @Select("SELECT * FROM product WHERE id = #{id}")
    Product selectById(Integer id);

    @Select("SELECT * FROM product WHERE id = #{id}")
    Product selectByIdIncludeOffline(Integer id);

    @Select("SELECT * FROM product WHERE merchant_id = #{merchantId} AND status = 1 ORDER BY sales DESC")
    List<Product> selectByMerchantId(Integer merchantId);

    @Select("SELECT * FROM product WHERE merchant_id = #{merchantId} ORDER BY sales DESC")
    List<Product> selectAllByMerchantId(Integer merchantId);

    @Select("SELECT * FROM product WHERE merchant_id = #{merchantId} AND category_id = #{categoryId} AND status = 1")
    List<Product> selectByMerchantAndCategory(@Param("merchantId") Integer merchantId,
                                               @Param("categoryId") Integer categoryId);

    @Update("UPDATE product SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteById(Integer id);

    @Update("UPDATE product SET sales = sales + 1 WHERE id = #{id}")
    int increaseSales(Integer id);

    @Update("UPDATE product SET rating = #{rating} WHERE id = #{id}")
    int updateRatingOnly(@Param("id") Integer id, @Param("rating") BigDecimal rating);

    @Update("UPDATE product SET review_count = review_count + 1 WHERE id = #{id}")
    int incrementReviewCount(@Param("id") Integer id);
}