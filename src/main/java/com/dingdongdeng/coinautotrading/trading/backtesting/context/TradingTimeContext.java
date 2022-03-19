package com.dingdongdeng.coinautotrading.trading.backtesting.context;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TradingTimeContext {

    private static final ThreadLocal<Context> contextThreadLocal = ThreadLocal.withInitial(Context::new);

    public static LocalDateTime now() {
        Context context = contextThreadLocal.get();
        if (Objects.isNull(context)) {
            Context newContext = new Context();
            contextThreadLocal.set(newContext);
            return newContext.getNowSupplier().get();
        }
        return context.getNowSupplier().get();

    }

    public static void nowSupplier(Supplier<LocalDateTime> supplier) {
        Context context = contextThreadLocal.get();
        context.setNowSupplier(supplier);
    }

    public static void clear() {
        contextThreadLocal.remove();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Context {

        private Supplier<LocalDateTime> nowSupplier = LocalDateTime::now;
    }

}
