package com.dingdongdeng.coinautotrading.trading.backtesting.service;

import com.dingdongdeng.coinautotrading.trading.backtesting.model.BackTestingRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BackTestingService {

    public String doTest(BackTestingRequest.Register request) {
        return UUID.randomUUID().toString(); // backTestingId
    }

    public Object getResult(String backTestingId) {
        return null;
    }
}
