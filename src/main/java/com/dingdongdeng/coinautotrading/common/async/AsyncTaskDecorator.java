package com.dingdongdeng.coinautotrading.common.async;

import com.dingdongdeng.coinautotrading.common.filter.LoggingUtils;
import java.util.Map;
import org.springframework.core.task.TaskDecorator;

public class AsyncTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable task) {
        Map<String, String> context = LoggingUtils.getLogData();
        return () -> {
            LoggingUtils.setLogData(context);
            task.run();
        };
    }
}