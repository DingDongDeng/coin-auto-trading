package com.dingdongdeng.coinautotrading.domain.repository;

import com.dingdongdeng.coinautotrading.domain.entity.ExchangeCandle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeCandleRepository extends JpaRepository<ExchangeCandle, Long> {

}
