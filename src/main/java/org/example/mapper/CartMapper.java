package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.domain.Cart;
import java.util.List;

@Mapper
public interface CartMapper {

    // 添加购物车
    @Insert("INSERT INTO cart(user_id, merchant_id, product_id, quantity) " +
            "VALUES(#{userId}, #{merchantId}, #{productId}, #{quantity})")
    int insert(Cart cart);

    // 查询购物车（简易）
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND merchant_id = #{merchantId}")
    List<Cart> selectByUserAndMerchant(@Param("userId") Integer userId,
                                       @Param("merchantId") Integer merchantId);

    // 查询购物车 + 商品信息（结算必须用）
    @Select("SELECT c.* FROM cart c " +
            "WHERE c.user_id = #{userId} AND c.merchant_id = #{merchantId}")
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "user_id", property = "userId"),
        @Result(column = "merchant_id", property = "merchantId"),
        @Result(column = "product_id", property = "productId"),
        @Result(column = "quantity", property = "quantity"),
        @Result(column = "product_id", property = "product",
                one = @One(select = "org.example.mapper.ProductMapper.selectByIdIncludeOffline"))
    })
    List<Cart> selectCartWithProduct(@Param("userId") Integer userId,
                                     @Param("merchantId") Integer merchantId);

    // 修改数量
    @Update("UPDATE cart SET quantity = #{quantity} WHERE id = #{id}")
    int updateQuantity(@Param("id") Integer id, @Param("quantity") Integer quantity);

    // 删除
    @Delete("DELETE FROM cart WHERE id = #{id}")
    int deleteById(Integer id);

    // 清空购物车
    @Delete("DELETE FROM cart WHERE user_id = #{userId} AND merchant_id = #{merchantId}")
    int clearCart(@Param("userId") Integer userId, @Param("merchantId") Integer merchantId);
}
