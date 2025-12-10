package org.vaskozlov.is.course;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.concurrent.TimeUnit;

@EnableJpaRepositories("org.vaskozlov.is.course.repository")
@EntityScan(basePackages = "org.vaskozlov.is.course.bean")
@SpringBootApplication(
        scanBasePackages = {"org.vaskozlov.is.course"}
)
@EnableCaching
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("eventsCache");
        cacheManager.setCaffeine(
                Caffeine
                        .newBuilder()
                        .expireAfterAccess(10, TimeUnit.MINUTES)
        );
        return cacheManager;
    }
}
