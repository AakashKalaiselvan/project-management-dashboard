package com.pms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class DatabaseConnectionTest {

    @Test
    void contextLoads() {
        // This test will verify that:
        // 1. Spring Boot application starts successfully
        // 2. Database connection is established
        // 3. Flyway migrations run successfully
        // 4. JPA entities are properly configured
    }
} 