package com.dingdongdeng.coinautotrading.domain.service;

import com.dingdongdeng.coinautotrading.common.domain.RepositoryService;
import com.dingdongdeng.coinautotrading.common.type.CandleUnit;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.domain.entity.Candle;
import com.dingdongdeng.coinautotrading.domain.repository.CandleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public Candle findLastCandle(CoinType coinType, CandleUnit candleUnit) {
//        return repository.findByCoinTypeAnd
        return null;
    }
}
