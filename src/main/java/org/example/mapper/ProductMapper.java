package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.domain.Product;
import java.util.List;

@Mapper
public interface ProductMapper {

    // 新增商品
    @Insert("INSERT INTO product(merchant_id, category_id, name, price, original_price, image, description, stock, sales, status, sort) " +
            "VALUES(#{merchantId}, #{categoryId}, #{name}, #{price}, #{originalPrice}, #{image}, #{description}, #{stock}, #{sales}, #{status}, #{sort})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);

    // 根据ID查询（正常商品）
    @Select("SELECT * FROM product WHERE id = #{id} AND status = 1")
    Product selectById(Integer id);

    // 根据ID查询（含已下架商品，供购物车关联使用）
    @Select("SELECT * FROM product WHERE id = #{id}")
    Product selectByIdIncludeOffline(Integer id);

    // 查询某个商家的【全部上架商品】
    @Select("SELECT * FROM product WHERE merchant_id = #{merchantId} AND status = 1")
    List<Product> selectByMerchantId(Integer merchantId);

    // 根据分类查询
    @Select("SELECT * FROM product WHERE merchant_id = #{merchantId} AND category_id = #{categoryId} AND status = 1")
    List<Product> selectByMerchantAndCategory(@Param("merchantId") Integer merchantId,
                                              @Param("categoryId") Integer categoryId);

    // 更新上下架状态
    @Update("UPDATE product SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    // 删除商品
    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteById(Integer id);

    // 更新销量
    @Update("UPDATE product SET sales = sales + 1 WHERE id = #{id}")
    int increaseSales(Integer id);
}
