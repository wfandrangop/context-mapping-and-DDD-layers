package com.veritrabajo.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    BackendApplicationTests.IN_MEMORY_PROFILE,
    BackendApplicationTests.AUTO_CONFIGURATION_EXCLUDES
})
class BackendApplicationTests {

    public static final String IN_MEMORY_PROFILE = "spring.profiles.active=in-memory";
    private static final String DATA_SOURCE_AUTO_CONFIGURATION =
            "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration";
    private static final String HIBERNATE_AUTO_CONFIGURATION =
            "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration";
    public static final String AUTO_CONFIGURATION_EXCLUDES =
            "spring.autoconfigure.exclude="
                    + DATA_SOURCE_AUTO_CONFIGURATION
                    + ","
                    + HIBERNATE_AUTO_CONFIGURATION;

    @Test
    void contextLoads() {
    }
}
