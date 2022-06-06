package com.dingdongdeng.coinautotrading.domain.entity;

import com.dingdongdeng.coinautotrading.common.type.OrderState;
import com.dingdongdeng.coinautotrading.common.type.OrderType;
import com.dingdongdeng.coinautotrading.common.type.PriceType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trade_order")
public class TradeOrder {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "trade_order_id")
  private Long id;

  @Column(name = "order_id", columnDefinition = "VARCHAR(30) COMMENT '주문 번호'")
  private String orderId;

  @Enumerated(EnumType.STRING)
  @Column(name = "order_type", columnDefinition = "VARCHAR(30) DEFAULT 'guest' COMMENT '주문 종류'")
  private OrderType orderType;

  @Enumerated(EnumType.STRING)
  @Column(name = "price_type", columnDefinition = "VARCHAR(30) DEFAULT 'guest' COMMENT '주문 방식'")
  private PriceType priceType;

  @Column(name = "price", columnDefinition = "DOUBLE DEFAULT 0 COMMENT '주문 당시 화폐 가격'")
  private Double price;

  @Column(name = "avg_price", columnDefinition = "DOUBLE DEFAULT 0 COMMENT '체결 가격의 평균가'")
  private Double avgPrice;

  @Enumerated(EnumType.STRING)
  @Column(name = "order_state", columnDefinition = "VARCHAR(30) DEFAULT 'WAIT' COMMENT '주문 상태'")
  private OrderState orderState;

  @Column(name = "market", columnDefinition = "VARCHAR(30) DEFAULT 'guest' COMMENT '이름'")
  private String market;

  @Column(name = "created_at", columnDefinition = "VARCHAR(30) DEFAULT 'guest' COMMENT '주문 생성 시간'")
  private String createdAt;

  @Column(name = "volume", columnDefinition = "DOUBLE DEFAULT 0 COMMENT '사용자가 입력한 주문 양'")
  private Double volume;

  @Column(name = "remaining_volume", columnDefinition = "DOUBLE DEFAULT 0 COMMENT '체결 후 남은 주문 양'")
  private Double remainingVolume;

  @Column(name = "reserved_fee", columnDefinition = "DOUBLE  DEFAULT 0 COMMENT '수수료로 예약된 비용'")
  private Double reservedFee;

  @Column(name = "remaining_fee", columnDefinition = "DOUBLE DEFAULT 0 COMMENT '남은 수수료'")
  private Double remainingFee;

  @Column(name = "paid_fee", columnDefinition = "DOUBLE DEFAULT 0 COMMENT '사용된 수수료'")
  private Double paidFee;

  @Column(name = "locked", columnDefinition = "DOUBLE DEFAULT 0 COMMENT '거래에 사용중인 비용'")
  private Double locked;

  @Column(name = "executed_volume", columnDefinition = "DOUBLE DEFAULT 0 COMMENT '체결된 양'")
  private Double executedVolume;

  @Column(name = "trade_count", columnDefinition = "VARCHAR(30) DEFAULT 0 COMMENT '해당 주문에 걸린 체결 수'")
  private Long tradeCount;
}
