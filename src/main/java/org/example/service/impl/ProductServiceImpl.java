package org.example.service.impl;

import org.example.domain.Product;
import org.example.mapper.ProductMapper;
import org.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public int addProduct(Product product) {
        // 默认值
        if (product.getStatus() == null) product.setStatus(1);
        if (product.getSales() == null) product.setSales(0);
        if (product.getSort() == null) product.setSort(0);
        return productMapper.insert(product);
    }

    @Override
    public Product getById(Integer id) {
        return productMapper.selectById(id);
    }

    @Override
    public List<Product> getByMerchantId(Integer merchantId) {
        return productMapper.selectByMerchantId(merchantId);
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
}