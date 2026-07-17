package org.example.service.impl;

import com.alibaba.fastjson.JSON;
import org.example.Common.CacheService;
import org.example.domain.Cart;
import org.example.domain.Product;
import org.example.mapper.CartMapper;
import org.example.mapper.ProductMapper;
import org.example.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.example.Common.RedisConstant.CART_KEY_PREFIX;
import static org.example.Common.RedisConstant.CART_TTL;

@Service
public class CartServiceImpl implements CartService {
    private final StringRedisTemplate stringRedisTemplate;
    private final CacheService cacheService;

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public CartServiceImpl(StringRedisTemplate stringRedisTemplate,
                           CacheService cacheService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.cacheService = cacheService;
    }

    @Override
    public void addCart(Cart cart) {
        cart.setId(null);
        cartMapper.insert(cart);
        Integer newId = cart.getId();
        if (cart.getProductId() != null) {
            Product p = productMapper.selectById(cart.getProductId());
            cart.setProduct(p);
        }
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
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(cartList), CART_TTL, TimeUnit.SECONDS);
    }

    private List<Cart> getCachedCart(Integer userId, Integer merchantId) {
        String key = CART_KEY_PREFIX + userId + ":" + merchantId;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null) {
            if ("empty_list".equals(json)) return new ArrayList<>();
            return JSON.parseArray(json, Cart.class);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Cart> getCartWithProduct(Integer userId, Integer merchantId) {
        String key = CART_KEY_PREFIX + userId + ":" + merchantId;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null) {
            if ("empty_list".equals(json)) return new ArrayList<>();
            List<Cart> list = JSON.parseArray(json, Cart.class);
            for (Cart c : list) {
                if (c.getProduct() == null && c.getProductId() != null) {
                    c.setProduct(productMapper.selectById(c.getProductId()));
                }
            }
            return list;
        }
        // cache miss, use CacheService with write-through
        List<Cart> list = cacheService.queryListWithProtect(key, Cart.class,
                () -> cartMapper.selectCartWithProduct(userId, merchantId),
                CART_TTL, TimeUnit.SECONDS);
        if (list != null) {
            for (Cart c : list) {
                if (c.getProduct() == null && c.getProductId() != null) {
                    c.setProduct(productMapper.selectById(c.getProductId()));
                }
            }
        }
        return list != null ? list : new ArrayList<>();
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
        stringRedisTemplate.delete(key);
        return cartMapper.clearCart(userId, merchantId) > 0;
    }
}