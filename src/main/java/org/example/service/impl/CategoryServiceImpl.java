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
        return categoryMapper.insert(category);
    }
    @Override
    public List<Category> getByMerchantId(Integer merchantId) {
        return categoryMapper.selectByMerchantId(merchantId);
    }
}