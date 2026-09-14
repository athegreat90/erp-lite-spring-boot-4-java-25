package de.alexandermora.erplite.infrastructure.persistence.redis;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;

import static de.alexandermora.erplite.commons.constant.CacheConstants.*;

@Configuration
@EnableCaching
public class RedisConfig {
    private static final Duration REDIS_CACHE_TTL = Duration.ofHours(24);

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        var genericJackson2JsonRedisSerializer = GenericJacksonJsonRedisSerializer.builder()
                .typePropertyName("_type").enableUnsafeDefaultTyping().build();
        var configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(REDIS_CACHE_TTL)
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(genericJackson2JsonRedisSerializer))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        var configsMap = new HashMap<String, RedisCacheConfiguration>();

        configsMap.put(CACHE_PRODUCTS_BY_ID, configuration);
        configsMap.put(CACHE_PRODUCTS_BY_SKU, configuration);
        configsMap.put(CACHE_PRODUCTS_BY_CATEGORY, configuration);
        configsMap.put(CACHE_PRODUCTS_ACTIVE, configuration);
        configsMap.put(CACHE_CATALOGS_BY_TYPE, configuration);
        configsMap.put(CACHE_CATALOGS_ITEMS, configuration);

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(configuration)
                .withInitialCacheConfigurations(configsMap)
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        var template = new RedisTemplate<String, Object>();
        var serializer = GenericJacksonJsonRedisSerializer.builder()
                .typePropertyName("_type").enableUnsafeDefaultTyping().build();

        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }
}
