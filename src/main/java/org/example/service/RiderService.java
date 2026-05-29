package org.example.service;

import org.example.domain.Rider;
import java.math.BigDecimal;
import java.util.List;

public interface RiderService {
    // 骑手注册入驻
    int addRider(Rider rider);

    // 根据骑手ID查询
    Rider getRiderById(Integer id);

    // 根据用户ID查询骑手信息
    Rider getRiderByUserId(Integer userId);

    // 获取所有在线骑手
    List<Rider> getOnlineRider();

    // 修改骑手状态
    boolean changeStatus(Integer riderId, Integer status);

    // 更新骑手评分
    boolean changeRating(Integer riderId, BigDecimal rating);
}