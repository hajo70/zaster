package de.spricom.zaster.importing;

import de.spricom.zaster.entities.managment.UserEntity;
import de.spricom.zaster.security.AuthenticatedUser;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RestController
@Log4j2
@AllArgsConstructor
public class ImportController {

    private final AuthenticatedUser authenticatedUser;

    @PostMapping("/api/upload-handler")
    void upload(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("upload called by {}",
                authenticatedUser.get().map(UserEntity::getName).orElse("no one"));
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            br.lines().limit(20).forEach(System.out::println);
        }
    }
}
