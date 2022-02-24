package com.dingdongdeng.coinautotrading.auth.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dingdongdeng.coinautotrading.ApiDocumentUtils;
import com.dingdongdeng.coinautotrading.auth.component.KeyService;
import com.dingdongdeng.coinautotrading.auth.model.KeyRegisterRequest;
import com.dingdongdeng.coinautotrading.auth.model.KeyRegisterRequest.KeyPair;
import com.dingdongdeng.coinautotrading.auth.model.KeyResponse;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class, MockitoExtension.class})
class AuthControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @SpyBean
    private KeyService keyService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    public void 거래소_키_등록_테스트() throws Exception {

        CoinExchangeType coinExchangeType = CoinExchangeType.UPBIT;
        String userId = "1234";
        List<KeyPair> keyPairList = List.of(new KeyPair("accessKey", "accessKey-123123123"), new KeyPair("secretKey", "secretKey-1231231223"));
        KeyRegisterRequest request = KeyRegisterRequest.builder()
            .coinExchangeType(coinExchangeType)
            .keyPairList(keyPairList)
            .build();

        Mockito.doReturn(
            keyPairList.stream().map(p -> KeyResponse.builder()
                .pairId(UUID.randomUUID().toString())
                .coinExchangeType(coinExchangeType)
                .name(p.getKeyName())
                .value(p.getValue())
                .build()).collect(Collectors.toList())
        )
            .when(keyService).register(Mockito.any(), Mockito.any());

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.post("/auth/key")
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk())
            .andDo(
                document("auth/key",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestHeaders(
                        headerWithName("userId").description("사용자ID")
                    ),
                    requestFields(
                        fieldWithPath("coinExchangeType").type(JsonFieldType.STRING).description("거래소 종류(upbit)"),
                        fieldWithPath("keyPairList[]").type(JsonFieldType.ARRAY).description("거래소 api 사용을 위한 키"),
                        fieldWithPath("keyPairList[].keyName").type(JsonFieldType.STRING).description("키 이름"),
                        fieldWithPath("keyPairList[].value").type(JsonFieldType.STRING).description("키 값")
                    ),
                    responseFields(
                        fieldWithPath("body[]").type(JsonFieldType.ARRAY).description("데이터").optional(),
                        fieldWithPath("body[].pairId").type(JsonFieldType.STRING).description("키 페어 ID"),
                        fieldWithPath("body[].coinExchangeType").type(JsonFieldType.STRING).description("거래소 종류(upbit)"),
                        fieldWithPath("body[].name").type(JsonFieldType.STRING).description("키 이름"),
                        fieldWithPath("body[].value").type(JsonFieldType.STRING).description("키 값"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지").optional()
                    )
                )
            )
            .andReturn();
    }

    @Test
    public void 유저_거래소_키_조회_테스트() throws Exception {

        CoinExchangeType coinExchangeType = CoinExchangeType.UPBIT;
        String userId = "1234";
        List<KeyPair> keyPairList = List.of(new KeyPair("accessKey", "accessKey-123123123"), new KeyPair("secretKey", "secretKey-1231231223"));

        Mockito.doReturn(
            keyPairList.stream().map(p -> KeyResponse.builder()
                .pairId(UUID.randomUUID().toString())
                .coinExchangeType(coinExchangeType)
                .name(p.getKeyName())
                .value(p.getValue())
                .build()).collect(Collectors.toList())
        )
            .when(keyService).getUserKeyList(Mockito.any());

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.get("/auth/user/{userId}/key", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
        )
            .andExpect(status().isOk())
            .andDo(
                document("auth/user/key",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestHeaders(
                        headerWithName("userId").description("사용자ID")
                    ),
                    pathParameters(
                        RequestDocumentation.parameterWithName("userId").description("사용자 ID")
                    ),
                    responseFields(
                        fieldWithPath("body[]").type(JsonFieldType.ARRAY).description("데이터").optional(),
                        fieldWithPath("body[].pairId").type(JsonFieldType.STRING).description("키 페어 ID"),
                        fieldWithPath("body[].coinExchangeType").type(JsonFieldType.STRING).description("거래소 종류(upbit)"),
                        fieldWithPath("body[].name").type(JsonFieldType.STRING).description("키 이름"),
                        fieldWithPath("body[].value").type(JsonFieldType.STRING).description("키 값"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지").optional()
                    )
                )
            )
            .andReturn();
    }

}