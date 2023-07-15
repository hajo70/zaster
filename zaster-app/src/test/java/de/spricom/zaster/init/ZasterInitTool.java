package de.spricom.zaster.init;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("initme")
public class ZasterInitTool {

    @Autowired
    private ZasterInitProperties props;

    @Test
    void checkProperties() {
        assertThat(props).isNotNull();
        assertThat(props.getTenants()).hasSize(1);
    }
}
