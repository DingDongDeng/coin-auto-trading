package com.dingdongdeng.coinautotrading.trading.domain.repository;

import com.dingdongdeng.coinautotrading.trading.domain.entity.Trading;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradingRepository extends JpaRepository<Trading, Long> {

}
