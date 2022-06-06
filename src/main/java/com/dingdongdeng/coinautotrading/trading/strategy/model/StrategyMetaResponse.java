package com.dingdongdeng.coinautotrading.trading.strategy.model;

import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import java.util.List;
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
public class StrategyMetaResponse {

  private StrategyCode strategyCode;
  private List<ParamMeta> paramMetaList;

  @ToString
  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ParamMeta {

    private String name;
    private String guideMessage;
    private Object type;
  }
}
