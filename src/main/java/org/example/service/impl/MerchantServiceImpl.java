package org.example.service.impl;

import org.example.Common.CacheService;
import org.example.domain.Merchant;
import org.example.domain.User;
import org.example.mapper.MerchantMapper;
import org.example.mapper.UserMapper;
import org.example.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.example.Common.RedisConstant.MERCHANT_LIST_KEY;
import static org.example.Common.RedisConstant.MERCHANT_TTL;

@Service
public class MerchantServiceImpl implements MerchantService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheService cacheService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    public MerchantServiceImpl(RedisTemplate<String, Object> redisTemplate,
                               CacheService cacheService) {
        this.redisTemplate = redisTemplate;
        this.cacheService = cacheService;
    }

    // ====================== 1. 创建商家（入驻） ======================
    @Override
    @Transactional
    public int createMerchant(Merchant merchant, Long userId) {
        // 1. 用户必须存在
        User user = userMapper.findById(userId);
        if (user == null) {
            return -1; // 用户不存在
        }

        // 2. 一个用户只能开一家店
        Merchant existing = merchantMapper.selectByUserId(userId);
        if (existing != null) {
            return -2; // 店铺已存在
        }

        // 3. 如果当前用户不是商家，自动升级角色为商家
        if (user.getRole() != 1) {
            userMapper.adminUpdateRole(user.getId(), 1, LocalDateTime.now());
        }

        // 4. 初始化店铺信息
        merchant.setUserId(userId.intValue());
        merchant.setRating(5.0);   // 默认满分
        merchant.setStatus(0);     // 默认未营业

        // 5. 插入数据库
        return merchantMapper.insert(merchant);
    }

    // ====================== 2. 根据ID查询商家 ======================
    @Override
    public Merchant getMerchantById(Long id) {
        if (id == null) return null;
        return merchantMapper.selectById(id);
    }

    // ====================== 3. 根据用户ID查询商家 ======================
    @Override
    public Merchant getMerchantByUserId(Long userId) {
        if (userId == null) return null;
        return merchantMapper.selectByUserId(userId);
    }

    // ====================== 4. 获取所有有效商家（未被封禁） ======================
    @Override
    public List<Merchant> getAllValidMerchants() {
        return cacheService.queryListWithProtect(MERCHANT_LIST_KEY, Merchant.class,
                () -> merchantMapper.selectValid(),
                MERCHANT_TTL, TimeUnit.SECONDS);
    }
    /** 门店变更时清除缓存 */
    public void evictMerchantCache() {
        redisTemplate.delete(MERCHANT_LIST_KEY);
    }

    // ====================== 5. 更新营业状态 ======================
    @Override
    public boolean updateMerchantStatus(Long id, Integer status) {
        if (id == null || status == null) return false;
        // 状态只能是 0、1、2、3
        if (status < 0 || status > 3) return false;

        return merchantMapper.updateStatus(id, status) > 0;
    }

    // ====================== 6. 更新店铺信息 ======================
    @Override
    public boolean updateMerchantInfo(Merchant merchant) {
        if (merchant == null || merchant.getId() == null) return false;

        // 不允许通过这个接口修改状态和评分
        Merchant old = merchantMapper.selectById(merchant.getId());
        if (old == null) return false;

        merchant.setStatus(old.getStatus());
        merchant.setRating(old.getRating());

        return merchantMapper.updateMerchantInfo(merchant) > 0;
    }

    // ====================== 7. 搜索店铺 ======================
    @Override
    public List<Merchant> searchMerchants(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllValidMerchants();
        }
        return merchantMapper.searchByShopName(keyword);
    }

    // ====================== 8. 分页查询（已补齐） ======================
    @Override
    public PageInfo<Merchant> getMerchantsByPage(Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        PageHelper.startPage(pageNum, pageSize);
        List<Merchant> list = merchantMapper.selectValid();
        return new PageInfo<>(list);
    }

    // ====================== 9. 更新店铺评分（完整校验） ======================
    @Override
    public boolean updateMerchantRating(Long id, Double newRating) {
        if (id == null || newRating == null) return false;

        // 评分必须在 0.0 ~ 5.0 之间
        if (newRating < 0 || newRating > 5) return false;

        // 店铺必须存在
        Merchant merchant = merchantMapper.selectById(id);
        if (merchant == null) return false;

        return merchantMapper.updateRating(id, newRating) > 0;
    }

    // ====================== 10. 封禁店铺 ======================
    @Override
    public boolean banMerchant(Long id) {
        if (id == null) return false;
        return merchantMapper.banMerchant(id) > 0;
    }
}