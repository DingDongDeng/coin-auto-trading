package com.dingdongdeng.coinautotrading.admin.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dingdongdeng.coinautotrading.ApiDocumentUtils;
import com.dingdongdeng.coinautotrading.admin.model.CommandRequest;
import com.dingdongdeng.coinautotrading.admin.model.type.Command;
import com.dingdongdeng.coinautotrading.admin.service.AdminService;
import com.dingdongdeng.coinautotrading.common.type.CoinExchangeType;
import com.dingdongdeng.coinautotrading.common.type.CoinType;
import com.dingdongdeng.coinautotrading.common.type.TradingTerm;
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
class AdminControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @SpyBean
    private AdminService adminService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    public void 커맨드_API_테스트() throws Exception {
        Command command = Command.START;
        CommandRequest request = CommandRequest.builder()
            .coinType(CoinType.ETHEREUM)
            .coinExchangeType(CoinExchangeType.UPBIT)
            .tradingTerm(TradingTerm.SCALPING)
            .strategyCode(StrategyCode.RSI)
            .build();

        Mockito.doNothing()
            .when(adminService).command(command, request);

        MvcResult result = this.mockMvc.perform(
            RestDocumentationRequestBuilders.post("/admin/command/{command}", command)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk())
            .andDo(
                document("admin/command",
                    ApiDocumentUtils.getDocumentRequest(),
                    ApiDocumentUtils.getDocumentResponse(),
                    pathParameters(
                        RequestDocumentation.parameterWithName("command").description("명령어 유형(START, STOP)")
                    ),
                    requestFields(
                        fieldWithPath("coinType").type(JsonFieldType.STRING).description("코인 종류(ETHEREUM)"),
                        fieldWithPath("coinExchangeType").type(JsonFieldType.STRING).description("거래소(UPBIT)"),
                        fieldWithPath("tradingTerm").type(JsonFieldType.STRING).description("매매 타입(EXTREAM_SCALPING, SCALPING, DAY, SWING)"),
                        fieldWithPath("strategyCode").type(JsonFieldType.STRING).description("매매 전략(RSI)")
                    ),
                    responseFields(
                        fieldWithPath("body").type(JsonFieldType.OBJECT).description("데이터").optional(),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("메세지").optional()
                    )
                )
            )
            .andReturn();
    }

}