package com.BramaSquare360.leggy.application;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication
@EnableMongoRepositories(basePackages = {
        "restaurant", "delivery", "restaurant.infrastructure.persistence",
        "user.infrastructure.persistence", "ordering", "recommendation.infrastructure.persistence",
        "payment", "delivery.infrastructure.persistence","shared.infrastructure"
})
@ComponentScan(
        basePackages = {
                "ordering", "delivery", "user", "restaurant",
                "recommendation", "payment", "ordering.infrastructure.mapper", "shared"
        }, 
                excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "shared\\.exception\\..*"
        )
)
@EnableAsync
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableMethodSecurity
@EnableCaching
public class LeggyApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeggyApplication.class, args);
    }
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

}
