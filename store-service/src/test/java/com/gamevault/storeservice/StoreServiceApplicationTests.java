package com.gamevault.storeservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

@EnableFeignClients(basePackages = "com.gamevault.storeservice.client")
@SpringBootTest
@ContextConfiguration(initializers = TestcontainersConfiguration.Initializer.class)
class StoreServiceApplicationTests {
    @Test
    void contextLoads() {}
}
