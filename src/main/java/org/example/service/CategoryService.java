package org.example.service;
import org.example.domain.Category;
import java.util.List;
public interface CategoryService {
    int addCategory(Category category);
    List<Category> getByMerchantId(Integer merchantId);
}