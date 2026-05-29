package org.example.service;

import org.example.domain.Cart;
import java.util.List;

public interface CartService {
    boolean addCart(Cart cart);
    List<Cart> getCartList(Integer userId, Integer merchantId);
    List<Cart> getCartWithProduct(Integer userId, Integer merchantId);
    boolean updateQuantity(Integer id, Integer quantity);
    boolean deleteCart(Integer id);
    boolean clearCart(Integer userId, Integer merchantId);
}