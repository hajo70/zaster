package de.spricom.zaster.init;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class LiquibaseSetupTest {

    @Test
    void testApplicationStarts() {
    }
}
