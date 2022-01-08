package com.dingdongdeng.coinautotrading.trading.domain.service;

import com.dingdongdeng.coinautotrading.common.domain.RepositoryService;
import com.dingdongdeng.coinautotrading.trading.domain.entity.Trading;
import com.dingdongdeng.coinautotrading.trading.domain.repository.TradingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class TradingService implements RepositoryService<Trading, Long> {

    private final TradingRepository repository;

    public Trading findById(Long id) {
        return repository.findById(id).orElse(new Trading());
    }

    public Trading save(Trading trading) {
        return repository.save(trading);
    }

    public List<Trading> saveAll(Iterable<Trading> iterable) {
        return repository.saveAll(iterable);
    }
}
