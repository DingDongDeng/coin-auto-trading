package com.dingdongdeng.coinautotrading.trading.domain.repository;

import com.dingdongdeng.coinautotrading.trading.domain.entity.ExchangeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeOrderRepository extends JpaRepository<ExchangeOrder, Long> {

}
