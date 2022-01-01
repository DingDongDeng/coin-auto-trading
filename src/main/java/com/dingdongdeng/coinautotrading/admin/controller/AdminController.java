package com.dingdongdeng.coinautotrading.admin.controller;

import com.dingdongdeng.coinautotrading.admin.service.AdminService;
import com.dingdongdeng.coinautotrading.admin.type.Command;
import com.dingdongdeng.coinautotrading.autotrading.strategy.model.type.StrategyCode;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/{coinExchangeType}")
    public String command(@PathVariable String coinExchangeType, @RequestParam String command, @RequestParam String strategy) {
        adminService.command(
            CoinExchangeType.valueOf(coinExchangeType.toUpperCase()),
            Command.valueOf(command.toUpperCase()),
            StrategyCode.valueOf(strategy.toUpperCase())
        );
        return "execute command : " + coinExchangeType + "/" + command;
    }
}
