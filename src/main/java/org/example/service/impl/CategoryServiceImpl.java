package org.example.service.impl;
import org.example.domain.Category;
import org.example.mapper.CategoryMapper;
import org.example.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public int addCategory(Category category) {
        // 检查是否已存在同名分类
        int count = categoryMapper.countByMerchantAndName(category.getMerchantId(), category.getName());
        if (count > 0) {
            throw new RuntimeException("该分类名称已存在");
        }
        return categoryMapper.insert(category);
    }
    @Override
    public List<Category> getByMerchantId(Integer merchantId) {
        return categoryMapper.selectByMerchantId(merchantId);
    }
}