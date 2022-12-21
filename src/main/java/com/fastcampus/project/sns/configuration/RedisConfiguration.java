package com.fastcampus.project.sns.configuration;

import com.fastcampus.project.sns.model.User;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@EnableRedisRepositories
@Configuration
public class RedisConfiguration {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisURI redisURI = RedisURI.create(redisProperties.getUrl());
        org.springframework.data.redis.connection.RedisConfiguration configuration =
                LettuceConnectionFactory.createRedisConfiguration(redisURI);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet();

        return factory;
    }

    // 캐싱을 하는 데이터는 변경이 많이 되지 않는 데이터를 사용해야 한다.
    // 접근이 많은 데이터를 사용하면 더 좋은 효과를 볼 수 있다.
    @Bean
    public RedisTemplate<String, User> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, User> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<User>(User.class));

        return redisTemplate;
    }

}
