package com.dingdongdeng.coinautotrading.autotrading.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "exchange_orders")
public class ExchangeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_order_id")
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(30) DEFAULT 'guest' COMMENT '이름'")
    private String name;

}
