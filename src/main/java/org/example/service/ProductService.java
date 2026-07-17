package org.example.service;

import org.example.domain.Product;
import java.util.List;

public interface ProductService {
    int addProduct(Product product);
    Product getById(Integer id);
    List<Product> getByMerchantId(Integer merchantId);
    List<Product> getByMerchantAndCategory(Integer merchantId, Integer categoryId);
    List<Product> getAllByMerchantId(Integer merchantId);
    boolean updateStatus(Integer id, Integer status);
    boolean deleteProduct(Integer id);
    boolean addSales(Integer id);
    boolean updateProduct(Product product);
}