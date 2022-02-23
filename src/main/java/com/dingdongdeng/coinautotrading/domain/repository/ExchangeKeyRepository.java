package com.dingdongdeng.coinautotrading.domain.repository;

import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeKeyRepository extends JpaRepository<ExchangeKey, Long> {

}
