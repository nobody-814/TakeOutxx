package org.example.service.impl;

import org.example.Common.CacheService;
import org.example.domain.Category;
import org.example.mapper.CategoryMapper;
import org.example.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.example.Common.RedisConstant.CATEGORY_LIST_KEY;
import static org.example.Common.RedisConstant.CATEGORY_TTL;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheService cacheService;

    @Autowired
    private CategoryMapper categoryMapper;

    public CategoryServiceImpl(RedisTemplate<String, Object> redisTemplate,
                               CacheService cacheService) {
        this.redisTemplate = redisTemplate;
        this.cacheService = cacheService;
    }

    @Override
    public int addCategory(Category category) {
        int count = categoryMapper.countByMerchantAndName(category.getMerchantId(), category.getName());
        if (count > 0) {
            return -1;
        }
        int result = categoryMapper.insert(category);
        redisTemplate.delete(CATEGORY_LIST_KEY + category.getMerchantId());
        return result;
    }

    @Override
    public Category getById(Integer id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public int deleteCategory(Integer categoryId, Integer merchantId) {
        int result = categoryMapper.deleteById(categoryId);
        redisTemplate.delete(CATEGORY_LIST_KEY + merchantId);
        return result;
    }

    @Override
    public List<Category> getByMerchantId(Integer merchantId) {
        String key = CATEGORY_LIST_KEY + merchantId;
        return cacheService.queryListWithProtect(key, Category.class,
                () -> categoryMapper.selectByMerchantId(merchantId),
                CATEGORY_TTL, TimeUnit.SECONDS);
    }
}