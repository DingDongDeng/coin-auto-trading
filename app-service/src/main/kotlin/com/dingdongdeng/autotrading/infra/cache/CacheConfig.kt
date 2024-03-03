package com.dingdongdeng.autotrading.infra.cache

import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    fun cacheManager(): CacheManager {
        return ConcurrentMapCacheManager(QUERY_CACHE_MANAGER)
    }

    companion object {
        const val QUERY_CACHE_MANAGER = "queryCacheManager"
    }
}