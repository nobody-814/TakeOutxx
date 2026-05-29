package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.domain.OrderItem;
import java.util.List;

@Mapper
public interface OrderItemMapper {

    // 添加订单项
    @Insert("INSERT INTO orderitem(order_id, product_id, product_name, product_price, quantity, total_price) " +
            "VALUES(#{orderId}, #{productId}, #{productName}, #{productPrice}, #{quantity}, #{totalPrice})")
    int insert(OrderItem orderItem);

    // 根据订单号查询所有商品
    @Select("SELECT * FROM orderitem WHERE order_id = #{orderId}")
    List<OrderItem> selectByOrderId(String orderId);
}