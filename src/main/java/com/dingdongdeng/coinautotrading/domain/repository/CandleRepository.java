package com.dingdongdeng.coinautotrading.domain.repository;

import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.entity.Candle;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CandleRepository extends JpaRepository<Candle, Long> {

    @Query("select c from Candle c "
        + "where c.coinType = :coinType and c.candleUnit = :candleUnit and c.coinExchangeType = :coinExchangeType "
        + "order by c.candleDateTimeKst desc ")
    List<Candle> findAllCandleList(
        @Param("coinType") CoinType coinType,
        @Param("candleUnit") CandleUnit candleUnit,
        @Param("coinExchangeType") CoinExchangeType coinExchangeType,
        Pageable pageable
    );
}
