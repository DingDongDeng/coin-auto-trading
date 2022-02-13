package com.dingdongdeng.coinautotrading.admin.controller;

import com.dingdongdeng.coinautotrading.admin.model.CommandRequest;
import com.dingdongdeng.coinautotrading.admin.model.type.Command;
import com.dingdongdeng.coinautotrading.admin.service.AdminService;
import com.dingdongdeng.coinautotrading.common.model.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/command/{command}")
    public ResponseEntity<CommonResponse<Void>> command(@PathVariable String command, @RequestBody CommandRequest request) {
        adminService.command(Command.of(command), request);
        return ResponseEntity.ok(
            CommonResponse.<Void>builder().message("execute command : " + command + "/" + request).build()
        );
    }
}
