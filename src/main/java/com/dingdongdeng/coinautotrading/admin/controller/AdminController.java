package com.dingdongdeng.coinautotrading.admin.controller;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.admin.type.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/{coinExchangeType}")
    public String command(@PathVariable String coinExchangeType, @RequestParam String command) {
        CoinExchangeType.valueOf(coinExchangeType.toUpperCase());
        Command.valueOf(command.toUpperCase());
        return "execute command : " + command;
    }
}
