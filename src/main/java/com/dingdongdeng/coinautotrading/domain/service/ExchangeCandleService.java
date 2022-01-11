package com.dingdongdeng.coinautotrading.domain.service;

import com.dingdongdeng.coinautotrading.common.domain.RepositoryService;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeCandle;
import com.dingdongdeng.coinautotrading.domain.repository.ExchangeCandleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class ExchangeCandleService implements RepositoryService<ExchangeCandle, Long> {

    private final ExchangeCandleRepository repository;

    public ExchangeCandle findById(Long id) {
        return repository.findById(id).orElse(new ExchangeCandle());
    }

    public ExchangeCandle save(ExchangeCandle entity) {
        return repository.save(entity);
    }

    public List<ExchangeCandle> saveAll(Iterable<ExchangeCandle> iterable) {
        return repository.saveAll(iterable);
    }
}
