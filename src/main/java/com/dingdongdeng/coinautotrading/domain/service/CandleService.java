package com.dingdongdeng.coinautotrading.domain.service;

import com.dingdongdeng.coinautotrading.common.domain.RepositoryService;
import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.entity.Candle;
import com.dingdongdeng.coinautotrading.domain.repository.CandleRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class CandleService implements RepositoryService<Candle, Long> {

    private final CandleRepository repository;

    public Candle findById(Long id) {
        return repository.findById(id).orElse(new Candle());
    }

    public Candle save(Candle entity) {
        return repository.save(entity);
    }

    public List<Candle> saveAll(Iterable<Candle> iterable) {
        return repository.saveAll(iterable);
    }

    public Optional<Candle> findOneLastCandle(CoinExchangeType coinExchangeType, CoinType coinType, CandleUnit candleUnit) {
        return repository.findAllCandleList(coinType, candleUnit, coinExchangeType, PageRequest.of(0, 1))
            .stream()
            .findFirst();
    }
}
