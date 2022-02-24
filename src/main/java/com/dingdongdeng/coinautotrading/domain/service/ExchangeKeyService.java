package com.dingdongdeng.coinautotrading.domain.service;

import com.dingdongdeng.coinautotrading.common.domain.RepositoryService;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.repository.ExchangeKeyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class ExchangeKeyService implements RepositoryService<ExchangeKey, Long> {

    private final ExchangeKeyRepository repository;

    @Override
    public ExchangeKey findById(Long id) {
        return repository.findById(id).orElse(new ExchangeKey());
    }

    @Override
    public ExchangeKey save(ExchangeKey entity) {
        return repository.save(entity);
    }

    @Override
    public List<ExchangeKey> saveAll(Iterable<ExchangeKey> iterable) {
        return repository.saveAll(iterable);
    }

    @Override
    public void delete(ExchangeKey entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteAll(Iterable<ExchangeKey> iterable) {
        repository.deleteAll(iterable);
    }

    public List<ExchangeKey> findByPairId(String pairId) {
        return repository.findByPairId(pairId);
    }

    public List<ExchangeKey> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }
}
