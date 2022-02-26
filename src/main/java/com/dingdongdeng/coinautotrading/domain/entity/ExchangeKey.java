package com.dingdongdeng.coinautotrading.domain.entity;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
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
@Table(name = "exchange_key")
public class ExchangeKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_key_id")
    private Long id;

    @Column(name = "pair_id")
    private String pairId;

    @Enumerated(EnumType.STRING)
    @Column(name = "coin_exchange_type")
    private CoinExchangeType coinExchangeType;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;

    @Column(name = "user_id")
    private String userId;

}
