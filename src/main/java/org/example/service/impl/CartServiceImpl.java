package org.example.service.impl;

import org.example.domain.Cart;
import org.example.mapper.CartMapper;
import org.example.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Override
    public boolean addCart(Cart cart) {
        return cartMapper.insert(cart) > 0;
    }

    @Override
    public List<Cart> getCartList(Integer userId, Integer merchantId) {
        return cartMapper.selectByUserAndMerchant(userId, merchantId);
    }

    @Override
    public List<Cart> getCartWithProduct(Integer userId, Integer merchantId) {
        return cartMapper.selectCartWithProduct(userId, merchantId);
    }

    @Override
    public boolean updateQuantity(Integer id, Integer quantity) {
        return cartMapper.updateQuantity(id, quantity) > 0;
    }

    @Override
    public boolean deleteCart(Integer id) {
        return cartMapper.deleteById(id) > 0;
    }

    @Override
    public boolean clearCart(Integer userId, Integer merchantId) {
        return cartMapper.clearCart(userId, merchantId) > 0;
    }
}