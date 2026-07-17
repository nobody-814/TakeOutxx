package org.example.service.impl;

import com.alibaba.fastjson.JSON;
import org.example.domain.Product;
import org.example.mapper.ProductMapper;
import org.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.example.Common.RedisConstant.*;

@Service
public class ProductServiceImpl implements ProductService {
    private final RedisTemplate redisTemplate;
    @Autowired
    private ProductMapper productMapper;

    public ProductServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public int addProduct(Product product) {
        if (product.getStatus() == null) product.setStatus(1);
        if (product.getSales() == null) product.setSales(0);
        if (product.getSort() == null) product.setSort(0);
        return productMapper.insert(product);
    }

    @Override
    public Product getById(Integer id) {
        String key = PRODUCT_DETAIL_KEY + id;
        Object val = redisTemplate.opsForValue().get(key);
        if (val != null) {
            return JSON.parseObject(val.toString(), Product.class);
        }
        Product product = productMapper.selectById(id);
        if (product != null) {
            redisTemplate.opsForValue().set(key, JSON.toJSONString(product), PRODUCT_TTL, TimeUnit.SECONDS);
        }
        return product;
    }

    @Override
    public List<Product> getByMerchantId(Integer merchantId) {
        String key = HOT_PRODUCTS_KEY + merchantId;
        Object val = redisTemplate.opsForValue().get(key);
        if (val != null) {
            return JSON.parseArray(val.toString(), Product.class);
        }
        List<Product> list = productMapper.selectByMerchantId(merchantId);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(list), HOT_TTL, TimeUnit.SECONDS);
        return list;
    }

    @Override
    public List<Product> getAllByMerchantId(Integer merchantId) {
        return productMapper.selectAllByMerchantId(merchantId);
    }

    public void evictProductCache(Integer merchantId) {
        redisTemplate.delete(HOT_PRODUCTS_KEY + merchantId);
    }

    public void evictProductDetailCache(Integer productId) {
        redisTemplate.delete(PRODUCT_DETAIL_KEY + productId);
    }

    @Override
    public List<Product> getByMerchantAndCategory(Integer merchantId, Integer categoryId) {
        return productMapper.selectByMerchantAndCategory(merchantId, categoryId);
    }

    @Override
    public boolean updateStatus(Integer id, Integer status) {
        return productMapper.updateStatus(id, status) > 0;
    }

    @Override
    public boolean deleteProduct(Integer id) {
        return productMapper.deleteById(id) > 0;
    }

    @Override
    public boolean addSales(Integer id) {
        return productMapper.increaseSales(id) > 0;
    }
    @Override
    public boolean updateProduct(Product product) {
        return productMapper.updateProduct(product) > 0;
    }
}