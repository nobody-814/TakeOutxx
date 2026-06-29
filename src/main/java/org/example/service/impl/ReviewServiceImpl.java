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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired private ReviewMapper reviewMapper;
    @Autowired private MerchantMapper merchantMapper;
    @Autowired private ProductMapper productMapper;
    @Autowired private RiderMapper riderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReview(Review review) {
        reviewMapper.insert(review);
        refreshRating(review.getType(), review.getMerchantId(),
                review.getProductId(), review.getRiderId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOrderReviews(String orderId, Integer userId, Integer merchantId,
                                    Integer riderId, List<Review> reviews) {
        for (Review r : reviews) {
            r.setOrderId(orderId);
            r.setUserId(userId);
            r.setMerchantId(merchantId);
            if (r.getType() == 3) r.setRiderId(riderId);
            reviewMapper.insert(r);
            refreshRating(r.getType(), merchantId, r.getProductId(), r.getRiderId());
        }
    }

    private void refreshRating(Integer type, Integer merchantId,
                                Integer productId, Integer riderId) {
        switch (type) {
            case 1 -> {
                BigDecimal avg = reviewMapper.avgMerchantRating(merchantId)
                        .setScale(1, RoundingMode.HALF_UP);
                merchantMapper.updateRatingOnly(merchantId, avg.doubleValue());
                try { merchantMapper.incrementReviewCount(merchantId); } catch (Exception ignored) {}
            }
            case 2 -> {
                BigDecimal avg = reviewMapper.avgProductRating(productId)
                        .setScale(1, RoundingMode.HALF_UP);
                productMapper.updateRatingOnly(productId, avg);
                try { productMapper.incrementReviewCount(productId); } catch (Exception ignored) {}
            }
            case 3 -> {
                BigDecimal avg = reviewMapper.avgRiderRating(riderId)
                        .setScale(1, RoundingMode.HALF_UP);
                riderMapper.updateRatingOnly(riderId, avg);
                try { riderMapper.incrementReviewCount(riderId); } catch (Exception ignored) {}
            }
        }
    }

    @Override public List<Review> getByMerchant(Integer mid) { return reviewMapper.selectByMerchantId(mid); }
    @Override public List<Review> getByProduct(Integer pid)  { return reviewMapper.selectByProductId(pid); }
    @Override public List<Review> getByRider(Integer rid)     { return reviewMapper.selectByRiderId(rid); }
    @Override public List<Review> getByOrder(String oid)      { return reviewMapper.selectByOrderId(oid); }
    @Override public boolean hasReviewed(String orderId) { return reviewMapper.countByOrderId(orderId) > 0; }
}
