package de.spricom.zaster;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("initdb")
public class DatabaseSetupTest {

    @Test
    void testDatabaseSchema() {

    }
}
