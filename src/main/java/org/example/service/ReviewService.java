package org.example.service;

import org.example.domain.Review;
import java.util.List;

public interface ReviewService {

    void submitReview(Review review);

    void submitOrderReviews(String orderId, Integer userId, Integer merchantId,
                            Integer riderId, List<Review> reviews);

    List<Review> getByMerchant(Integer merchantId);

    List<Review> getByProduct(Integer productId);

    List<Review> getByRider(Integer riderId);

    List<Review> getByOrder(String orderId);

    boolean hasReviewed(String orderId);
}
