package com.dingdongdeng.coinautotrading.common.slack;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Component
public class SlackSender {

    @Value("${spring.profiles.active:}")
    private String profile;
    @Value("${slack.webHookUrl:}")
    private String webHookUrl;
    private final WebClient slackWebClient;

    /**
     * fixme processor의 에러 발생하면 슬랙보내게하기 health check 응답안오면 슬랙보내게하기
     */
    public void send(Throwable throwable) {
        if (!profile.equalsIgnoreCase("release")) {
            return;
        }
        Map<String, String> body = new HashMap<>();
        body.put("username", "coinautotrading");
        body.put("text", throwable.getMessage());
        try {
            slackWebClient.post()
                .uri(webHookUrl)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .retry(2)
                .subscribe(
                    res -> log.info("slack sended"),
                    e -> log.error(e.getMessage(), e)
                );
        } catch (Exception e) {
            log.error("Failed to send slack message. error : {}, message : {}", e, body);
        }
    }
}
