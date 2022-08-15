package com.dingdongdeng.coinautotrading.auth.controller;

import com.dingdongdeng.coinautotrading.auth.model.KeyPairRegisterRequest;
import com.dingdongdeng.coinautotrading.auth.model.KeyPairResponse;
import com.dingdongdeng.coinautotrading.auth.service.KeyService;
import com.dingdongdeng.coinautotrading.common.model.CommonResponse;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@RequiredArgsConstructor
@RestController
public class KeyController {

    private final KeyService keyService;

    @GetMapping("/user/key/pair")
    public CommonResponse<List<KeyPairResponse>> getKeyList(@SessionAttribute String userId) {
        return CommonResponse.<List<KeyPairResponse>>builder()
            .body(keyService.getUserKeyList(userId))
            .message("key get success")
            .build();
    }

    @PostMapping("/key/pair")
    public CommonResponse<List<KeyPairResponse>> registerKey(@Valid @RequestBody KeyPairRegisterRequest request, @SessionAttribute String userId) {
        return CommonResponse.<List<KeyPairResponse>>builder()
            .body(keyService.register(request, userId))
            .message("key register success")
            .build();
    }

    @DeleteMapping("/key/pair/{keyPairId}")
    public CommonResponse<List<KeyPairResponse>> deleteKey(@PathVariable String keyPairId, @SessionAttribute String userId) {
        return CommonResponse.<List<KeyPairResponse>>builder()
            .body(keyService.deleteKeyPair(keyPairId, userId))
            .message("key register success")
            .build();
    }

}
