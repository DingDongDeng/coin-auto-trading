package com.dingdongdeng.coinautotrading.domain.service;

import com.dingdongdeng.coinautotrading.common.domain.RepositoryService;
import com.dingdongdeng.coinautotrading.domain.entity.TradeOrder;
import com.dingdongdeng.coinautotrading.domain.repository.TradeOrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class TradeOrderService implements RepositoryService<TradeOrder, Long> {

    private final TradeOrderRepository repository;

    @Override
    public TradeOrder findById(Long id) {
        return repository.findById(id).orElse(new TradeOrder());
    }

    @Override
    public TradeOrder save(TradeOrder tradeOrder) {
        return repository.save(tradeOrder);
    }

    @Override
    public List<TradeOrder> saveAll(Iterable<TradeOrder> iterable) {
        return repository.saveAll(iterable);
    }

    @Override
    public void delete(TradeOrder entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteAll(Iterable<TradeOrder> iterable) {
        repository.deleteAll(iterable);
    }
}
