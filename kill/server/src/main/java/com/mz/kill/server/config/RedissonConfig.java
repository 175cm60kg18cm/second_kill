package com.mz.kill.server.config;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RedissonConfig {
    @Autowired
    Environment environment;
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress(environment.getProperty("redis.config.host")).
                setPassword(environment.getProperty("spring.redis.password"));
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
