package org.example.service.impl;

import org.example.domain.Review;
import org.example.mapper.MerchantMapper;
import org.example.mapper.ProductMapper;
import org.example.mapper.ReviewMapper;
import org.example.mapper.RiderMapper;
import org.example.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired private ReviewMapper reviewMapper;
    @Autowired private MerchantMapper merchantMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private RiderMapper riderMapper;

    @Override
    public void submitReview(Review review) {
        reviewMapper.insert(review);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOrderReviews(String orderId, Integer userId, Integer merchantId,
                                    Integer riderId, List<Review> reviews) {
        for (Review r : reviews) {
            r.setOrderId(orderId);
            r.setUserId(userId);
            if (r.getType() == 1) r.setMerchantId(merchantId);
            if (r.getType() == 3) r.setRiderId(riderId);
            reviewMapper.insert(r);
            refreshRating(r.getType(), merchantId, r.getProductId(), riderId);
        }
    }

    private void refreshRating(Integer type, Integer merchantId,
                                Integer productId, Integer riderId) {
        switch (type) {
            case 1 -> merchantMapper.refreshRatingAndCount(merchantId);
            case 2 -> productMapper.refreshRatingAndCount(productId);
            case 3 -> riderMapper.refreshRatingAndCount(riderId);
        }
    }

    @Override public List<Review> getByMerchant(Integer mid) { return reviewMapper.selectByMerchantId(mid); }
    @Override public List<Review> getByProduct(Integer pid) { return reviewMapper.selectByProductId(pid); }
    @Override public List<Review> getByRider(Integer rid) { return reviewMapper.selectByRiderId(rid); }
    @Override public List<Review> getByOrder(String oid)      { return reviewMapper.selectByOrderId(oid); }
    @Override public boolean hasReviewed(String orderId) { return reviewMapper.countByOrderId(orderId) > 0; }
}