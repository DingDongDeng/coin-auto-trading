package com.dingdongdeng.coinautotrading.trading.strategy.repository;

import com.dingdongdeng.coinautotrading.trading.strategy.model.TradingTask;
import org.springframework.data.repository.CrudRepository;


public interface TradingTaskRepository extends CrudRepository<TradingTask, String> {

}
