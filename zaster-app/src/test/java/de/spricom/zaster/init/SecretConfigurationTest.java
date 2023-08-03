package de.spricom.zaster.init;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;

import static org.assertj.core.api.Assertions.assertThat;

class SecretConfigurationTest {
    private final File secreteFile = new File("config/secrets/application.properties");

    @Test
    void ensureSecret() throws IOException {
        if (!secreteFile.exists()) {
            Files.createDirectories(secreteFile.getParentFile().toPath());
            String key = createKey();
            String content = String.format("de.spricom.zaster.auth.secret=%s%n", key);
            Files.writeString(secreteFile.toPath(), content, StandardOpenOption.CREATE_NEW);
        }
        assertThat(secreteFile).exists();
    }

    private String createKey() {
        return RandomStringUtils.random(80, 65, 122, true, true, null, new SecureRandom());
    }
}