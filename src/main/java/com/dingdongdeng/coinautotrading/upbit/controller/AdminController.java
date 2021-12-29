package com.dingdongdeng.coinautotrading.upbit.controller;

import com.dingdongdeng.coinautotrading.upbit.type.Command;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/upbit")
public class AdminController {

    @GetMapping("/command")
    public String command(@RequestParam String commandStr) {
        Command command = Command.valueOf(commandStr);
        return "execute command : " + commandStr;
    }
}
