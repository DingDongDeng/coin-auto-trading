package com.dingdongdeng.coinautotrading.domain.repository;

import com.dingdongdeng.coinautotrading.domain.entity.Candle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandleRepository extends JpaRepository<Candle, Long> {

}
