package org.example.service;

import org.example.domain.Merchant;
import com.github.pagehelper.PageInfo;
import java.util.List;

public interface MerchantService {
    // 1. 店铺创建（商家入驻）
    int createMerchant(Merchant merchant, Long userId);

    // 2. 按ID查询店铺（用于店铺详情）
    Merchant getMerchantById(Long id);

    // 3. 按商家用户ID查询店铺（关联当前登录用户）
    Merchant getMerchantByUserId(Long userId);

    // 4. 查询所有有效店铺（消费者端列表）
    List<Merchant> getAllValidMerchants();

    // 5. 更新店铺状态（营业/休息/关闭）
    boolean updateMerchantStatus(Long id, Integer status);

    // 6. 更新店铺基础信息（名称、地址等）
    boolean updateMerchantInfo(Merchant merchant);

    // 7. 搜索店铺（按名称模糊查询）
    List<Merchant> searchMerchants(String keyword);

    // 8. 分页查询店铺（支持消费者端分页加载）
    PageInfo<Merchant> getMerchantsByPage(Integer pageNum, Integer pageSize);

    // 9. 更新店铺评分（订单完成后触发）
    boolean updateMerchantRating(Long id, Double newRating);

    // 10. 封禁店铺（管理员操作）
    boolean banMerchant(Long id);
}