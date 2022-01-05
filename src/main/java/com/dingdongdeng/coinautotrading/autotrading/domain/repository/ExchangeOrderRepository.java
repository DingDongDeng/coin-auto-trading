package com.dingdongdeng.coinautotrading.autotrading.domain.repository;

import com.dingdongdeng.coinautotrading.autotrading.domain.entity.ExchangeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeOrderRepository extends JpaRepository<ExchangeOrder, Long> {

}
