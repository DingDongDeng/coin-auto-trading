package com.dingdongdeng.coinautotrading.domain.repository;

import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExchangeKeyRepository extends JpaRepository<ExchangeKey, Long> {

  List<ExchangeKey> findByPairId(String pairId);

  List<ExchangeKey> findByUserId(String userId);
}
