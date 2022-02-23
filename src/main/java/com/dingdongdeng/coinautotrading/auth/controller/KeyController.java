package com.dingdongdeng.coinautotrading.auth.controller;

import com.dingdongdeng.coinautotrading.auth.component.KeyService;
import com.dingdongdeng.coinautotrading.auth.model.KeyRegisterRequest;
import com.dingdongdeng.coinautotrading.auth.model.KeyResponse;
import com.dingdongdeng.coinautotrading.common.model.CommonResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/key")
public class KeyController {

    private final KeyService keyService;

    @PostMapping("")
    public CommonResponse<List<KeyResponse>> register(@RequestBody KeyRegisterRequest request, @RequestHeader String userId) {
        return CommonResponse.<List<KeyResponse>>builder()
            .body(keyService.register(request, userId))
            .message("key register success")
            .build();
    }

}
