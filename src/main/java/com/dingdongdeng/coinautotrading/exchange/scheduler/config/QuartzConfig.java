package com.dingdongdeng.coinautotrading.exchange.scheduler.config;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import com.dingdongdeng.coinautotrading.exchange.scheduler.UpbitCandleStoreJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    public static class UpbitQuartzConfig {

        @Bean
        public JobDetail upbitCandleStoreJobDetail() {
            return JobBuilder.newJob().ofType(UpbitCandleStoreJob.class)
                .storeDurably()
                .withIdentity("Qrtz_Job_Detail")
                .withDescription("Invoke Sample Job service...")
                .build();
        }

        @Bean
        public Trigger upbitCandleStoreTrigger(JobDetail upbitCandleStoreJobDetail) {
            return TriggerBuilder.newTrigger().forJob(upbitCandleStoreJobDetail)
                .withIdentity("Qrtz_Trigger")
                .withDescription("Sample trigger")
                .withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(5))
                .build();
        }
    }
}
