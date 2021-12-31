package com.dingdongdeng.coinautotrading.autotrading.strategy;

import com.dingdongdeng.coinautotrading.autotrading.strategy.type.StrategyCode;
import com.dingdongdeng.coinautotrading.autotrading.type.OrderType;

public abstract class Strategy {

    abstract public StrategyCode getCode();

    abstract public void execute();

    abstract protected OrderType what();

    abstract protected boolean when(OrderType orderType);

    abstract protected double how(OrderType orderType);

}
