package com.dingdongdeng.autotrading.infra.cache

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit


@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun cacheManager(caffeine: Caffeine<Any, Any>): CacheManager {
        val caffeineCacheManager = CaffeineCacheManager()
        caffeineCacheManager.setCaffeine(caffeine)
        return caffeineCacheManager
    }

    @Bean
    fun caffeine(): Caffeine<Any, Any> {
        return Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
    }

    companion object {
        const val QUERY_CACHE_MANAGER = "queryCacheManager"
    }
}