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

    @Update("UPDATE product SET sales = sales + #{quantity} WHERE id = #{id}")
    int increaseSalesByQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);

    @Update("UPDATE product SET stock = stock - #{quantity} WHERE id = #{id} AND stock >= #{quantity}")
    int decreaseStock(@Param("id") Integer id, @Param("quantity") Integer quantity);

    @Update("UPDATE product SET rating = (SELECT ROUND(AVG(rating), 1) FROM review WHERE product_id = #{id} AND type = 2), review_count = (SELECT COUNT(*) FROM review WHERE product_id = #{id} AND type = 2) WHERE id = #{id}")
    int refreshRatingAndCount(@Param("id") Integer id);

    @Update("UPDATE product SET name = #{name}, price = #{price}, original_price = #{originalPrice}, description = #{description}, stock = #{stock}, category_id = #{categoryId}, sort = #{sort} WHERE id = #{id}")
    int updateProduct(Product product);

    @Update("UPDATE product SET stock = stock + #{quantity}, sales = sales - #{quantity} WHERE id = #{id}")
    int rollbackStock(@Param("id") Integer id, @Param("quantity") Integer quantity);
}