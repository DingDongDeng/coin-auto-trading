package com.dingdongdeng.coinautotrading.console.controller.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypeInfoResponse {

  private List<TypeInfoTemplate> coinTypeList;
  private List<TypeInfoTemplate> coinExchangeTypeList;
  private List<TypeInfoTemplate> tradingTermList;
  private List<TypeInfoTemplate> strategyCodeList;

  @ToString
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TypeInfoTemplate {

    private String name;
    private Object value;

    public static List<TypeInfoTemplate> listOf(Map<?, String> enumMap) {
      return enumMap.entrySet().stream()
          .map(entry -> new TypeInfoTemplate(entry.getValue(), entry.getKey()))
          .collect(Collectors.toList());
    }
  }
}
