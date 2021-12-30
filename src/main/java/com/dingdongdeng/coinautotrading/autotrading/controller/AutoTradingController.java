package com.dingdongdeng.coinautotrading.autotrading.controller;

import com.dingdongdeng.coinautotrading.autotrading.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.autotrading.type.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auto-trading")
public class AutoTradingController {

    @GetMapping("/{coinExchangeType}")
    public String command(@PathVariable String coinExchangeType, @RequestParam String command) {
        CoinExchangeType.valueOf(coinExchangeType.toUpperCase());
        Command.valueOf(command.toUpperCase());
        return "execute command : " + command;
    }
}
