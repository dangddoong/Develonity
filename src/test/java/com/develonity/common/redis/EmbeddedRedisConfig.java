package com.develonity.common.redis;

import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Profile("test")
@Configuration
class EmbeddedRedisConfig {

  @Value("${spring.redis.port}")
  private int redisPort;
  private RedisServer redisServer;

  @PostConstruct
  public void redisServer() throws IOException {
    redisServer = RedisServer.builder()

            .port(redisPort)
            .setting("maxmemory 128M")
            .build();
            
    redisServer.start();
  }

  @PreDestroy
  public void stopRedis() {
    if (redisServer != null) {
      redisServer.stop();
    }
  }
}
