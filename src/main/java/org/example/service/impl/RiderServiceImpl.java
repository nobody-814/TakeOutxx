package org.example.service.impl;

import org.example.domain.Rider;
import org.example.domain.User;
import org.example.mapper.RiderMapper;
import org.example.mapper.UserMapper;
import org.example.service.RiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RiderServiceImpl implements RiderService {

    @Autowired
    private RiderMapper riderMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public int addRider(Rider rider) {
        Rider exist = riderMapper.selectByUserId(rider.getUserId());
        if (exist != null) return -2;

        User user = userMapper.findById(rider.getUserId().longValue());
        if (user == null) return -1;

        if (user.getRole() != 2) {
            userMapper.adminUpdateRole(user.getId(), 2, LocalDateTime.now());
        }

        rider.setStatus(0);
        rider.setRating(new BigDecimal("5.0"));
        return riderMapper.insert(rider);
    }


    @Override
    public Rider getRiderById(Integer id) {
        if (id == null) {
            return null;
        }
        return riderMapper.selectById(id);
    }

    @Override
    public Rider getRiderByUserId(Integer userId) {
        if (userId == null) {
            return null;
        }
        return riderMapper.selectByUserId(userId);
    }

    @Override
    public List<Rider> getOnlineRider() {
        return riderMapper.selectOnlineRider();
    }

    @Override
    public boolean changeStatus(Integer riderId, Integer status) {
        // 状态合法性校验：0/1/2
        if (riderId == null || status < 0 || status > 2) {
            return false;
        }
        return riderMapper.updateStatus(riderId, status) > 0;
    }

    @Override
    public boolean changeRating(Integer riderId, BigDecimal rating) {
        // 评分范围 0 ~ 5
        if (riderId == null || rating == null || rating.compareTo(BigDecimal.ZERO) < 0 || rating.compareTo(new BigDecimal("5.0")) > 0) {
            return false;
        }
        return riderMapper.updateRating(riderId, rating) > 0;
    }
}