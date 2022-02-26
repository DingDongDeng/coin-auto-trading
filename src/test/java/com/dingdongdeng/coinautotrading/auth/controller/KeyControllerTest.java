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

import com.dingdongdeng.coinautotrading.auth.model.Key;
import com.dingdongdeng.coinautotrading.auth.model.KeyPairRegisterRequest;
import com.dingdongdeng.coinautotrading.auth.model.KeyPairResponse;
import com.dingdongdeng.coinautotrading.auth.service.KeyService;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.doc.ApiDocumentUtils;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.repository.ExchangeKeyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
class KeyControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @SpyBean
    private KeyService keyService;
    @Autowired
    private ExchangeKeyRepository exchangeKeyRepository;

    @Value("${upbit.client.accessKey}")
    private String accessKey;
    @Value("${upbit.client.secretKey}")
    private String secretKey;

    private String userId = "123456";
    private String keyPairId;


    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();

        //fixme 많은 테스트 코드에서 아래 코드가 중복됨
        String keyPairId = UUID.randomUUID().toString();
        exchangeKeyRepository.save(
            ExchangeKey.builder()
                .pairId(keyPairId)
                .coinExchangeType(CoinExchangeType.UPBIT)
                .name("ACCESS_KEY")
                .value(accessKey)
                .userId(userId)
                .build()
        );

        exchangeKeyRepository.save(
            ExchangeKey.builder()
                .pairId(keyPairId)
                .coinExchangeType(CoinExchangeType.UPBIT)
                .name("SECRET_KEY")
                .value(secretKey)
                .userId(userId)
                .build()
        );

        this.keyPairId = keyPairId;
    }

    @Test
    public void 거래소_키_등록_테스트() throws Exception {

        CoinExchangeType coinExchangeType = CoinExchangeType.UPBIT;
        String userId = "1234";
        List<Key> keyList = List.of(new Key("accessKey", "accessKey-123123123"), new Key("secretKey", "secretKey-1231231223"));
        KeyPairRegisterRequest request = KeyPairRegisterRequest.builder()
            .coinExchangeType(coinExchangeType)
            .keyList(keyList)
            .build();

        Mockito.doReturn(
            List.of(
                KeyPairResponse.builder()
                    .pairId(UUID.randomUUID().toString())
                    .coinExchangeType(coinExchangeType)
                    .keyList(keyList)
                    .build()
            )
        )
            .when(keyService).register(Mockito.any(), Mockito.any());

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.post("/key")
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk())
            .andDo(
                document("key",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestHeaders(
                        headerWithName("userId").description("사용자ID")
                    ),
                    requestFields(
                        fieldWithPath("coinExchangeType").type(JsonFieldType.STRING).description("거래소 종류(upbit)"),
                        fieldWithPath("keyList[]").type(JsonFieldType.ARRAY).description("거래소 api 사용을 위한 키"),
                        fieldWithPath("keyList[].name").type(JsonFieldType.STRING).description("키 이름"),
                        fieldWithPath("keyList[].value").type(JsonFieldType.STRING).description("키 값")
                    ),
                    responseFields(
                        fieldWithPath("body[]").type(JsonFieldType.ARRAY).description("데이터").optional(),
                        fieldWithPath("body[].pairId").type(JsonFieldType.STRING).description("키 페어 ID"),
                        fieldWithPath("body[].coinExchangeType").type(JsonFieldType.STRING).description("거래소 종류(upbit)"),
                        fieldWithPath("body[].keyList[]").type(JsonFieldType.ARRAY).description("키 리스트"),
                        fieldWithPath("body[].keyList[].name").type(JsonFieldType.STRING).description("키 이름"),
                        fieldWithPath("body[].keyList[].value").type(JsonFieldType.STRING).description("키 값(마스킹)"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지").optional()
                    )
                )
            )
            .andReturn();
    }

    @Test
    public void 사용자_거래소_키_조회_테스트() throws Exception {

        CoinExchangeType coinExchangeType = CoinExchangeType.UPBIT;
        String userId = "1234";
        List<Key> keyList = List.of(new Key("accessKey", "accessKey-123123123"), new Key("secretKey", "secretKey-1231231223"));

        Mockito.doReturn(
            List.of(
                KeyPairResponse.builder()
                    .pairId(UUID.randomUUID().toString())
                    .coinExchangeType(coinExchangeType)
                    .keyList(keyList)
                    .build()
            )
        )
            .when(keyService).getUserKeyList(Mockito.any());

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.get("/user/{userId}/key", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
        )
            .andExpect(status().isOk())
            .andDo(
                document("user/key",
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
                        fieldWithPath("body[].keyList[]").type(JsonFieldType.ARRAY).description("키 리스트"),
                        fieldWithPath("body[].keyList[].name").type(JsonFieldType.STRING).description("키 이름"),
                        fieldWithPath("body[].keyList[].value").type(JsonFieldType.STRING).description("키 값(마스킹)"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지").optional()
                    )
                )
            )
            .andReturn();
    }

    @Test
    public void 거래소_키_삭제_테스트() throws Exception {

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/key/pair/{pairKeyId}", keyPairId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
        )
            .andExpect(status().isOk())
            .andDo(
                document("key/pair",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestHeaders(
                        headerWithName("userId").description("사용자ID")
                    ),
                    pathParameters(
                        RequestDocumentation.parameterWithName("pairKeyId").description("키페어 ID")
                    ),
                    responseFields(
                        fieldWithPath("body[]").type(JsonFieldType.ARRAY).description("데이터").optional(),
                        fieldWithPath("body[].pairId").type(JsonFieldType.STRING).description("키 페어 ID"),
                        fieldWithPath("body[].coinExchangeType").type(JsonFieldType.STRING).description("거래소 종류(upbit)"),
                        fieldWithPath("body[].keyList[]").type(JsonFieldType.ARRAY).description("키 리스트"),
                        fieldWithPath("body[].keyList[].name").type(JsonFieldType.STRING).description("키 이름"),
                        fieldWithPath("body[].keyList[].value").type(JsonFieldType.STRING).description("키 값(마스킹)"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지").optional()
                    )
                )
            )
            .andReturn();
    }

}