package com.dingdongdeng.coinautotrading.trading.domain.component;

import com.dingdongdeng.coinautotrading.common.domain.RepositoryComponent;
import com.dingdongdeng.coinautotrading.trading.domain.entity.ExchangeOrder;
import com.dingdongdeng.coinautotrading.trading.domain.repository.ExchangeOrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class ExchangeOrderComponent implements RepositoryComponent<ExchangeOrder, Long> {

    private final ExchangeOrderRepository repository;

    public ExchangeOrder findById(Long id) {
        return repository.findById(id).orElse(new ExchangeOrder());
    }

    public ExchangeOrder save(ExchangeOrder exchangeOrder) {
        return repository.save(exchangeOrder);
    }

    public List<ExchangeOrder> saveAll(Iterable<ExchangeOrder> iterable) {
        return repository.saveAll(iterable);
    }
}
