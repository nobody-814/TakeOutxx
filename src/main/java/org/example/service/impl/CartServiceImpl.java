package org.example.service.impl;

import com.alibaba.fastjson.JSON;
import org.example.domain.Cart;
import org.example.domain.Product;
import org.example.mapper.CartMapper;
import org.example.mapper.ProductMapper;
import org.example.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.example.Common.RedisConstant.CART_KEY_PREFIX;
import static org.example.Common.RedisConstant.CART_TTL;

@Service
public class CartServiceImpl implements CartService {
    private final RedisTemplate redisTemplate;

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public CartServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void addCart(Cart cart) {
        // 1. 入库获取 id
        cart.setId(null);
        cartMapper.insert(cart);
        Integer newId = cart.getId();
        // 2. 补全商品信息
        if (cart.getProductId() != null) {
            Product p = productMapper.selectById(cart.getProductId());
            cart.setProduct(p);
        }
        // 3. 同步 Redis 缓存
        String key = CART_KEY_PREFIX + cart.getUserId() + ":" + cart.getMerchantId();
        List<Cart> cartList = getCachedCart(cart.getUserId(), cart.getMerchantId());
        boolean exists = false;
        for (Cart c : cartList) {
            if (c.getProductId().equals(cart.getProductId())) {
                c.setQuantity(c.getQuantity() + cart.getQuantity());
                exists = true;
                break;
            }
        }
        if (!exists) {
            cart.setId(newId);
            cartList.add(cart);
        }
        redisTemplate.opsForValue().set(key, JSON.toJSONString(cartList), CART_TTL, TimeUnit.SECONDS);
    }

    private List<Cart> getCachedCart(Integer userId, Integer merchantId) {
        String key = CART_KEY_PREFIX + userId + ":" + merchantId;
        Object val = redisTemplate.opsForValue().get(key);
        if (val != null) {
            return JSON.parseArray(val.toString(), Cart.class);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Cart> getCartWithProduct(Integer userId, Integer merchantId) {
        List<Cart> cached = getCachedCart(userId, merchantId);
        if (!cached.isEmpty()) {
            // 补全商品信息（缓存中没有 product 对象）
            for (Cart c : cached) {
                if (c.getProduct() == null && c.getProductId() != null) {
                    c.setProduct(productMapper.selectById(c.getProductId()));
                }
            }
            return cached;
        }
        List<Cart> list = cartMapper.selectCartWithProduct(userId, merchantId);
        if (list == null) list = new ArrayList<>();
        return list;
    }

    @Override
    public List<Cart> getCartList(Integer userId, Integer merchantId) {
        return cartMapper.selectByUserAndMerchant(userId, merchantId);
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
        String key = CART_KEY_PREFIX + userId + ":" + merchantId;
        redisTemplate.delete(key);
        // 同步清除 DB 中的购物车
        List<Cart> cached = getCachedCart(userId, merchantId);
        for (Cart c : cached) {
            if (c.getId() != null) cartMapper.deleteById(c.getId());
        }
        return cartMapper.clearCart(userId, merchantId) > 0;
    }
}