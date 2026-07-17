package org.example.service;
import org.example.domain.Category;
import java.util.List;
public interface CategoryService {
    int addCategory(Category category);
    Category getById(Integer id);
    List<Category> getByMerchantId(Integer merchantId);
    int deleteCategory(Integer categoryId, Integer merchantId);
}