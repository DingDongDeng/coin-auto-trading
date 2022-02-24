package com.dingdongdeng.coinautotrading.console.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ConsoleController {

    @RequestMapping("/")
    public String index() {
        return "index";
    }
}
