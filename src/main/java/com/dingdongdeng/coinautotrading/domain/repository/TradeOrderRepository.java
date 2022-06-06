package com.dingdongdeng.coinautotrading.domain.repository;

import com.dingdongdeng.coinautotrading.domain.entity.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {}
