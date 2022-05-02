package com.dingdongdeng.coinautotrading.trading.autotrading.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.doc.ApiDocumentUtils;
import com.dingdongdeng.coinautotrading.domain.entity.ExchangeKey;
import com.dingdongdeng.coinautotrading.domain.repository.ExchangeKeyRepository;
import com.dingdongdeng.coinautotrading.trading.autotrading.aggregation.AutoTradingAggregation;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingRegisterRequest;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingResponse;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.type.AutoTradingProcessStatus;
import com.dingdongdeng.coinautotrading.trading.exchange.client.UpbitClient;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
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
class AutoTradingControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @SpyBean
    private AutoTradingAggregation autoTradingAggregation;

    @Autowired
    private ExchangeKeyRepository exchangeKeyRepository;
    @Autowired
    private UpbitClient upbitClient;
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
    public void 사용자_자동매매_리스트_조회_테스트() throws Exception {

        String autoTradingProcessorId = "abawefawef-awefawefawe-awefawefwaef";

        String title = "RSI 30이하 매매";
        CoinType coinType = CoinType.ETHEREUM;
        CoinExchangeType coinExchangeType = CoinExchangeType.UPBIT;
        String strategyIdentifyCode = StrategyCode.SCALE_TRADING_RSI.name() + ":" + UUID.randomUUID().toString();

        Mockito.doReturn(
            List.of(
                AutoTradingResponse.builder()
                    .title(title)
                    .processorId(autoTradingProcessorId)
                    .processDuration(1000)
                    .processStatus(AutoTradingProcessStatus.RUNNING)
                    .userId(userId)
                    .strategyIdentifyCode(strategyIdentifyCode)
                    .coinType(coinType)
                    .coinExchangeType(coinExchangeType)
                    .build(),
                AutoTradingResponse.builder()
                    .title(title)
                    .processorId(autoTradingProcessorId)
                    .processDuration(1000)
                    .processStatus(AutoTradingProcessStatus.RUNNING)
                    .userId(userId)
                    .strategyIdentifyCode(strategyIdentifyCode)
                    .coinType(coinType)
                    .coinExchangeType(coinExchangeType)
                    .build()
            )
        )
            .when(autoTradingAggregation).getUserProcessorList(Mockito.any());

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.get("/user/{userId}/autotrading", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
        )
            .andExpect(status().isOk())
            .andDo(
                document("user/autotrading",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestHeaders(
                        headerWithName("userId").description("사용자ID")
                    ),
                    pathParameters(
                        RequestDocumentation.parameterWithName("userId").description("사용자ID")
                    ),
                    responseFields(
                        fieldWithPath("body[]").type(JsonFieldType.ARRAY).description("데이터").optional(),
                        fieldWithPath("body[].title").type(JsonFieldType.STRING).description("사용자가 등록한 자동매매 이름"),
                        fieldWithPath("body[].processorId").type(JsonFieldType.STRING).description("자동매매 프로세스 ID"),
                        fieldWithPath("body[].processDuration").type(JsonFieldType.NUMBER).description("프로세스 동작 간격"),
                        fieldWithPath("body[].processStatus").type(JsonFieldType.STRING).description("자동매매 프로세스 상태"),
                        fieldWithPath("body[].userId").type(JsonFieldType.STRING).description("사용자ID"),
                        fieldWithPath("body[].strategyIdentifyCode").type(JsonFieldType.STRING).description("매매 전략 코드 (RSI)"),
                        fieldWithPath("body[].coinType").type(JsonFieldType.STRING).description("자동매매 할 코인 종류 (ETHEREUM, DOGE ...)"),
                        fieldWithPath("body[].coinExchangeType").type(JsonFieldType.STRING).description("자동거래에 사용할 거래소 종류(upbit)"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지").optional()
                    )
                )
            )
            .andReturn();
    }

    @Test
    public void 자동매매_등록_테스트() throws Exception {

        String processorId = "abawefawef-awefawefawe-awefawefwaef";

        String title = "RSI 30이하 매매";
        CoinType coinType = CoinType.ETHEREUM;
        CoinExchangeType coinExchangeType = CoinExchangeType.UPBIT;
        String strategyIdentifyCode = StrategyCode.SCALE_TRADING_RSI.name() + ":" + UUID.randomUUID().toString();

        AutoTradingRegisterRequest request = AutoTradingRegisterRequest.builder()
            .title(title)
            .coinType(CoinType.ETHEREUM)
            .coinExchangeType(CoinExchangeType.UPBIT)
            .tradingTerm(TradingTerm.SCALPING)
            .strategyCode(StrategyCode.SCALE_TRADING_RSI)
            .keyPairId(keyPairId)
            .build();

        Mockito.doReturn(
            AutoTradingResponse.builder()
                .title(title)
                .processorId(processorId)
                .processDuration(1000)
                .processStatus(AutoTradingProcessStatus.INIT)
                .userId(userId)
                .strategyIdentifyCode(strategyIdentifyCode)
                .coinType(coinType)
                .coinExchangeType(coinExchangeType)
                .build()
        )
            .when(autoTradingAggregation).register(Mockito.any(), Mockito.any());

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.post("/autotrading/register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk())
            .andDo(
                document("autotrading/register",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestHeaders(
                        headerWithName("userId").description("사용자ID")
                    ),
                    requestFields(
                        fieldWithPath("title").type(JsonFieldType.STRING).description("사용자가 등록한 자동매매 이름"),
                        fieldWithPath("coinType").type(JsonFieldType.STRING).description("자동매매 할 코인 종류 (ETHEREUM, DOGE ...)"),
                        fieldWithPath("coinExchangeType").type(JsonFieldType.STRING).description("자동거래에 사용할 거래소 종류(upbit)"),
                        fieldWithPath("tradingTerm").type(JsonFieldType.STRING).description("자동매매의 매매타입 (EXTREME_SCALPING, SCALPING,DAY,SWING)"),
                        fieldWithPath("strategyCode").type(JsonFieldType.STRING).description("매매 전략 코드 (RSI)"),
                        fieldWithPath("keyPairId").type(JsonFieldType.STRING).description("자동매매에 사용할 거래소 키페어 ID")
                    ),
                    responseFields(
                        fieldWithPath("body").type(JsonFieldType.OBJECT).description("데이터").optional(),
                        fieldWithPath("body.title").type(JsonFieldType.STRING).description("사용자가 등록한 자동매매 이름"),
                        fieldWithPath("body.processorId").type(JsonFieldType.STRING).description("자동매매 프로세스 ID"),
                        fieldWithPath("body.processDuration").type(JsonFieldType.NUMBER).description("프로세스 동작 간격"),
                        fieldWithPath("body.processStatus").type(JsonFieldType.STRING).description("자동매매 프로세스 상태"),
                        fieldWithPath("body.userId").type(JsonFieldType.STRING).description("사용자ID"),
                        fieldWithPath("body.strategyIdentifyCode").type(JsonFieldType.STRING).description("매매 전략 코드 (RSI)"),
                        fieldWithPath("body.coinType").type(JsonFieldType.STRING).description("자동매매 할 코인 종류 (ETHEREUM, DOGE ...)"),
                        fieldWithPath("body.coinExchangeType").type(JsonFieldType.STRING).description("자동거래에 사용할 거래소 종류(upbit)"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지").optional()
                    )
                )
            )
            .andReturn();
    }

    @Test
    public void 자동매매_시작_테스트() throws Exception {

        String autoTradingProcessorId = "abawefawef-awefawefawe-awefawefwaef";

        String title = "RSI 30이하 매매";
        CoinType coinType = CoinType.ETHEREUM;
        CoinExchangeType coinExchangeType = CoinExchangeType.UPBIT;
        String strategyIdentifyCode = StrategyCode.SCALE_TRADING_RSI.name() + ":" + UUID.randomUUID().toString();

        Mockito.doReturn(
            AutoTradingResponse.builder()
                .title(title)
                .processorId(autoTradingProcessorId)
                .processDuration(1000)
                .processStatus(AutoTradingProcessStatus.RUNNING)
                .userId(userId)
                .strategyIdentifyCode(strategyIdentifyCode)
                .coinType(coinType)
                .coinExchangeType(coinExchangeType)
                .build()
        )
            .when(autoTradingAggregation).start(Mockito.any(), Mockito.any());

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.post("/autotrading/{autoTradingProcessorId}/start", autoTradingProcessorId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
        )
            .andExpect(status().isOk())
            .andDo(
                document("autotrading/start",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestHeaders(
                        headerWithName("userId").description("사용자ID")
                    ),
                    pathParameters(
                        RequestDocumentation.parameterWithName("autoTradingProcessorId").description("자동매매 프로세스 ID")
                    ),
                    responseFields(
                        fieldWithPath("body").type(JsonFieldType.OBJECT).description("데이터").optional(),
                        fieldWithPath("body.title").type(JsonFieldType.STRING).description("사용자가 등록한 자동매매 이름"),
                        fieldWithPath("body.processorId").type(JsonFieldType.STRING).description("자동매매 프로세스 ID"),
                        fieldWithPath("body.processDuration").type(JsonFieldType.NUMBER).description("프로세스 동작 간격"),
                        fieldWithPath("body.processStatus").type(JsonFieldType.STRING).description("자동매매 프로세스 상태"),
                        fieldWithPath("body.userId").type(JsonFieldType.STRING).description("사용자ID"),
                        fieldWithPath("body.strategyIdentifyCode").type(JsonFieldType.STRING).description("매매 전략 코드 (RSI)"),
                        fieldWithPath("body.coinType").type(JsonFieldType.STRING).description("자동매매 할 코인 종류 (ETHEREUM, DOGE ...)"),
                        fieldWithPath("body.coinExchangeType").type(JsonFieldType.STRING).description("자동거래에 사용할 거래소 종류(upbit)"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지").optional()
                    )
                )
            )
            .andReturn();
    }

    @Test
    public void 자동매매_정지_테스트() throws Exception {
        String autoTradingProcessorId = "abawefawef-awefawefawe-awefawefwaef";

        String title = "RSI 30이하 매매";
        CoinType coinType = CoinType.ETHEREUM;
        CoinExchangeType coinExchangeType = CoinExchangeType.UPBIT;
        String strategyIdentifyCode = StrategyCode.SCALE_TRADING_RSI.name() + ":" + UUID.randomUUID().toString();

        Mockito.doReturn(
            AutoTradingResponse.builder()
                .title(title)
                .processorId(autoTradingProcessorId)
                .processDuration(1000)
                .processStatus(AutoTradingProcessStatus.STOPPED)
                .userId(userId)
                .strategyIdentifyCode(strategyIdentifyCode)
                .coinType(coinType)
                .coinExchangeType(coinExchangeType)
                .build()
        )
            .when(autoTradingAggregation).stop(Mockito.any(), Mockito.any());

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.post("/autotrading/{autoTradingProcessorId}/stop", autoTradingProcessorId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
        )
            .andExpect(status().isOk())
            .andDo(
                document("autotrading/stop",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestHeaders(
                        headerWithName("userId").description("사용자ID")
                    ),
                    pathParameters(
                        RequestDocumentation.parameterWithName("autoTradingProcessorId").description("자동매매 프로세스 ID")
                    ),
                    responseFields(
                        fieldWithPath("body").type(JsonFieldType.OBJECT).description("데이터").optional(),
                        fieldWithPath("body.title").type(JsonFieldType.STRING).description("사용자가 등록한 자동매매 이름"),
                        fieldWithPath("body.processorId").type(JsonFieldType.STRING).description("자동매매 프로세스 ID"),
                        fieldWithPath("body.processDuration").type(JsonFieldType.NUMBER).description("프로세스 동작 간격"),
                        fieldWithPath("body.processStatus").type(JsonFieldType.STRING).description("자동매매 프로세스 상태"),
                        fieldWithPath("body.userId").type(JsonFieldType.STRING).description("사용자ID"),
                        fieldWithPath("body.strategyIdentifyCode").type(JsonFieldType.STRING).description("매매 전략 코드 (RSI)"),
                        fieldWithPath("body.coinType").type(JsonFieldType.STRING).description("자동매매 할 코인 종류 (ETHEREUM, DOGE ...)"),
                        fieldWithPath("body.coinExchangeType").type(JsonFieldType.STRING).description("자동거래에 사용할 거래소 종류(upbit)"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지").optional()
                    )
                )
            )
            .andReturn();
    }

    @Test
    public void 자동매매_제거_테스트() throws Exception {
        String autoTradingProcessorId = "abawefawef-awefawefawe-awefawefwaef";

        String title = "RSI 30이하 매매";
        CoinType coinType = CoinType.ETHEREUM;
        CoinExchangeType coinExchangeType = CoinExchangeType.UPBIT;
        String strategyIdentifyCode = StrategyCode.SCALE_TRADING_RSI.name() + ":" + UUID.randomUUID().toString();

        Mockito.doReturn(
            AutoTradingResponse.builder()
                .title(title)
                .processorId(autoTradingProcessorId)
                .processDuration(1000)
                .processStatus(AutoTradingProcessStatus.TERMINATED)
                .userId(userId)
                .strategyIdentifyCode(strategyIdentifyCode)
                .coinType(coinType)
                .coinExchangeType(coinExchangeType)
                .build()
        )
            .when(autoTradingAggregation).terminate(Mockito.any(), Mockito.any());

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.post("/autotrading/{autoTradingProcessorId}/terminate", autoTradingProcessorId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
        )
            .andExpect(status().isOk())
            .andDo(
                document("autotrading/terminate",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    requestHeaders(
                        headerWithName("userId").description("사용자ID")
                    ),
                    pathParameters(
                        RequestDocumentation.parameterWithName("autoTradingProcessorId").description("자동매매 프로세스 ID")
                    ),
                    responseFields(
                        fieldWithPath("body").type(JsonFieldType.OBJECT).description("데이터").optional(),
                        fieldWithPath("body.title").type(JsonFieldType.STRING).description("사용자가 등록한 자동매매 이름"),
                        fieldWithPath("body.processorId").type(JsonFieldType.STRING).description("자동매매 프로세스 ID"),
                        fieldWithPath("body.processDuration").type(JsonFieldType.NUMBER).description("프로세스 동작 간격"),
                        fieldWithPath("body.processStatus").type(JsonFieldType.STRING).description("자동매매 프로세스 상태"),
                        fieldWithPath("body.userId").type(JsonFieldType.STRING).description("사용자ID"),
                        fieldWithPath("body.strategyIdentifyCode").type(JsonFieldType.STRING).description("매매 전략 코드 (RSI)"),
                        fieldWithPath("body.coinType").type(JsonFieldType.STRING).description("자동매매 할 코인 종류 (ETHEREUM, DOGE ...)"),
                        fieldWithPath("body.coinExchangeType").type(JsonFieldType.STRING).description("자동거래에 사용할 거래소 종류(upbit)"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지").optional()
                    )
                )
            )
            .andReturn();
    }
}