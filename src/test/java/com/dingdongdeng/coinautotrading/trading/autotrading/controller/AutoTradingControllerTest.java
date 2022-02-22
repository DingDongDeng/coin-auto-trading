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

import com.dingdongdeng.coinautotrading.ApiDocumentUtils;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingRegisterRequest;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.AutoTradingResponse;
import com.dingdongdeng.coinautotrading.trading.autotrading.model.type.AutoTradingProcessStatus;
import com.dingdongdeng.coinautotrading.trading.autotrading.service.AutoTradingService;
import com.dingdongdeng.coinautotrading.trading.strategy.model.type.StrategyCode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class AutoTradingControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @SpyBean
    private AutoTradingService autoTradingService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    public void 자동매매_등록_테스트() throws Exception {

        String processorId = "abawefawef-awefawefawe-awefawefwaef";

        String title = "RSI 30이하 매매";
        String userId = "1234";
        CoinType coinType = CoinType.ETHEREUM;
        CoinExchangeType coinExchangeType = CoinExchangeType.UPBIT;
        StrategyCode strategyCode = StrategyCode.RSI;

        AutoTradingRegisterRequest request = AutoTradingRegisterRequest.builder()
            .title(title)
            .coinType(CoinType.ETHEREUM)
            .coinExchangeType(CoinExchangeType.UPBIT)
            .tradingTerm(TradingTerm.SCALPING)
            .strategyCode(StrategyCode.RSI)
            .build();

        Mockito.doReturn(
            AutoTradingResponse.builder()
                .title(title)
                .processorId(processorId)
                .processDuration(1000)
                .processStatus(AutoTradingProcessStatus.INIT)
                .userId(userId)
                .strategyCode(strategyCode)
                .coinType(coinType)
                .coinExchangeType(coinExchangeType)
                .build()
        )
            .when(autoTradingService).register(Mockito.any(), Mockito.any());

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.post("/trading/autotrading/register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk())
            .andDo(
                document("trading/autotrading/register",
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
                        fieldWithPath("strategyCode").type(JsonFieldType.STRING).description("매매 전략 코드 (RSI)")
                    ),
                    responseFields(
                        fieldWithPath("body").type(JsonFieldType.OBJECT).description("데이터").optional(),
                        fieldWithPath("body.title").type(JsonFieldType.STRING).description("사용자가 등록한 자동매매 이름"),
                        fieldWithPath("body.processorId").type(JsonFieldType.STRING).description("자동매매 프로세스 ID"),
                        fieldWithPath("body.processDuration").type(JsonFieldType.NUMBER).description("프로세스 동작 간격"),
                        fieldWithPath("body.processStatus").type(JsonFieldType.STRING).description("자동매매 프로세스 상태"),
                        fieldWithPath("body.userId").type(JsonFieldType.STRING).description("사용자ID"),
                        fieldWithPath("body.strategyCode").type(JsonFieldType.STRING).description("매매 전략 코드 (RSI)"),
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
        String userId = "1234";
        CoinType coinType = CoinType.ETHEREUM;
        CoinExchangeType coinExchangeType = CoinExchangeType.UPBIT;
        StrategyCode strategyCode = StrategyCode.RSI;

        Mockito.doReturn(
            AutoTradingResponse.builder()
                .title(title)
                .processorId(autoTradingProcessorId)
                .processDuration(1000)
                .processStatus(AutoTradingProcessStatus.RUNNING)
                .userId(userId)
                .strategyCode(strategyCode)
                .coinType(coinType)
                .coinExchangeType(coinExchangeType)
                .build()
        )
            .when(autoTradingService).start(Mockito.any(), Mockito.any());

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.post("/trading/autotrading/{autoTradingProcessorId}/start", autoTradingProcessorId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
        )
            .andExpect(status().isOk())
            .andDo(
                document("trading/autotrading/start",
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
                        fieldWithPath("body.strategyCode").type(JsonFieldType.STRING).description("매매 전략 코드 (RSI)"),
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
        String userId = "1234";
        CoinType coinType = CoinType.ETHEREUM;
        CoinExchangeType coinExchangeType = CoinExchangeType.UPBIT;
        StrategyCode strategyCode = StrategyCode.RSI;

        Mockito.doReturn(
            AutoTradingResponse.builder()
                .title(title)
                .processorId(autoTradingProcessorId)
                .processDuration(1000)
                .processStatus(AutoTradingProcessStatus.STOPPED)
                .userId(userId)
                .strategyCode(strategyCode)
                .coinType(coinType)
                .coinExchangeType(coinExchangeType)
                .build()
        )
            .when(autoTradingService).stop(Mockito.any(), Mockito.any());

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.post("/trading/autotrading/{autoTradingProcessorId}/stop", autoTradingProcessorId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
        )
            .andExpect(status().isOk())
            .andDo(
                document("trading/autotrading/stop",
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
                        fieldWithPath("body.strategyCode").type(JsonFieldType.STRING).description("매매 전략 코드 (RSI)"),
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
        String userId = "1234";
        CoinType coinType = CoinType.ETHEREUM;
        CoinExchangeType coinExchangeType = CoinExchangeType.UPBIT;
        StrategyCode strategyCode = StrategyCode.RSI;

        Mockito.doReturn(
            AutoTradingResponse.builder()
                .title(title)
                .processorId(autoTradingProcessorId)
                .processDuration(1000)
                .processStatus(AutoTradingProcessStatus.TERMINATED)
                .userId(userId)
                .strategyCode(strategyCode)
                .coinType(coinType)
                .coinExchangeType(coinExchangeType)
                .build()
        )
            .when(autoTradingService).terminate(Mockito.any(), Mockito.any());

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.post("/trading/autotrading/{autoTradingProcessorId}/terminate", autoTradingProcessorId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("userId", userId)
        )
            .andExpect(status().isOk())
            .andDo(
                document("trading/autotrading/terminate",
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
                        fieldWithPath("body.strategyCode").type(JsonFieldType.STRING).description("매매 전략 코드 (RSI)"),
                        fieldWithPath("body.coinType").type(JsonFieldType.STRING).description("자동매매 할 코인 종류 (ETHEREUM, DOGE ...)"),
                        fieldWithPath("body.coinExchangeType").type(JsonFieldType.STRING).description("자동거래에 사용할 거래소 종류(upbit)"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지").optional()
                    )
                )
            )
            .andReturn();
    }
}