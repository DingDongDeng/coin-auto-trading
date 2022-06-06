package com.dingdongdeng.coinautotrading.auth.service;

import static java.util.stream.Collectors.groupingBy;

import com.dingdongdeng.coinautotrading.auth.model.Key;
import com.dingdongdeng.coinautotrading.auth.model.KeyPairRegisterRequest;
import com.dingdongdeng.coinautotrading.auth.model.KeyPairResponse;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.service.ExchangeKeyService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KeyService {

  private final ExchangeKeyService exchangeKeyService;

  public List<KeyPairResponse> register(KeyPairRegisterRequest request, String userId) {
    List<Key> keyList = request.getKeyList();
    String pairId = UUID.randomUUID().toString();
    List<ExchangeKey> exchangeKeyList =
        exchangeKeyService.saveAll(
            keyList.stream()
                .map(
                    k ->
                        ExchangeKey.builder()
                            .pairId(pairId)
                            .coinExchangeType(request.getCoinExchangeType())
                            .name(k.getName().trim())
                            .value(k.getValue().trim())
                            .userId(userId)
                            .build())
                .collect(Collectors.toList()));
    return makeKeyPairResponseList(exchangeKeyList);
  }

  public List<KeyPairResponse> getKeyList(String keyPairId) {
    return makeKeyPairResponseList(exchangeKeyService.findByPairId(keyPairId));
  }

  public List<KeyPairResponse> getUserKeyList(String userId) {
    return makeKeyPairResponseList(exchangeKeyService.findByUserId(userId));
  }

  public List<KeyPairResponse> deleteKeyPair(String keyPairId, String userId) {
    List<ExchangeKey> exchangeKeyList = exchangeKeyService.findByPairId(keyPairId);
    if (exchangeKeyList.stream().noneMatch(key -> key.getUserId().equals(userId))) {
      throw new RuntimeException("잘못된 접근입니다.");
    }
    exchangeKeyService.deleteAll(exchangeKeyList);
    return makeKeyPairResponseList(exchangeKeyList);
  }

  private List<KeyPairResponse> makeKeyPairResponseList(List<ExchangeKey> exchangeKeyList) {
    if (exchangeKeyList.isEmpty()) {
      return Collections.EMPTY_LIST;
    }
    Map<String, List<ExchangeKey>> groupedExchangeKeyMap =
        exchangeKeyList.stream().collect(groupingBy(ExchangeKey::getPairId));

    return groupedExchangeKeyMap.values().stream()
        .map(this::makeKeyPairResponse)
        .collect(Collectors.toList());
  }

  private KeyPairResponse makeKeyPairResponse(List<ExchangeKey> exchangeKeyList) {
    if (exchangeKeyList.isEmpty()) {
      return KeyPairResponse.builder().keyList(Collections.EMPTY_LIST).build();
    }
    ExchangeKey exchangeKey = exchangeKeyList.get(0);
    return KeyPairResponse.builder()
        .coinExchangeType(exchangeKey.getCoinExchangeType())
        .pairId(exchangeKey.getPairId())
        .keyList(
            exchangeKeyList.stream()
                .map(k -> Key.builder().name(k.getName()).value("security value").build())
                .collect(Collectors.toList()))
        .build();
  }
}
