package org.example.service.impl;

import com.alibaba.fastjson.JSON;
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

    private final RedisTemplate redisTemplate;

    @Autowired
    private CategoryMapper categoryMapper;

    public CategoryServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public int addCategory(Category category) {
        int count = categoryMapper.countByMerchantAndName(category.getMerchantId(), category.getName());
        if (count > 0) {
            throw new RuntimeException("该分类名称已存在");
        }
        int result = categoryMapper.insert(category);
        // 新增后清除缓存
        redisTemplate.delete(CATEGORY_LIST_KEY + category.getMerchantId());
        return result;
    }

    @Override
    public List<Category> getByMerchantId(Integer merchantId) {
        String key = CATEGORY_LIST_KEY + merchantId;
        Object val = redisTemplate.opsForValue().get(key);
        if (val != null) {
            return JSON.parseArray(val.toString(), Category.class);
        }
        List<Category> list = categoryMapper.selectByMerchantId(merchantId);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(list), CATEGORY_TTL, TimeUnit.SECONDS);
        return list;
    }
}