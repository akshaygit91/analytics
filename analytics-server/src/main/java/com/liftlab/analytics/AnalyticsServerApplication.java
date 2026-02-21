package com.liftlab.analytics;

import com.liftlab.analytics.config.AnalyticsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AnalyticsProperties.class)
public class AnalyticsServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnalyticsServerApplication.class, args);
    }
}