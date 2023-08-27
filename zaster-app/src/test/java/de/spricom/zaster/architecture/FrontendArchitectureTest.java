package de.spricom.zaster.architecture;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

public class FrontendArchitectureTest {
    private static final Path FRONTEND_DIR = Path.of("frontend");

    private static final SortedMap<String, String> fileContents = new TreeMap<>();

    @BeforeAll
    static void readFiles() throws IOException {
        Files.walkFileTree(FRONTEND_DIR, new SimpleFileVisitor<>()         {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                return dir.startsWith(Path.of("frontend", "generated"))
                        ? FileVisitResult.SKIP_SUBTREE
                        : FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                readFile(file);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void readFile(Path file) throws IOException {
        String name = file.toString().substring("frontend/".length());
        String content = Files.readString(file);
        fileContents.put(name, content);
    }

    @Test
    @Disabled
    void dump() {
        fileContents.forEach(this::dumpImports);
    }

    private void dumpImports(String file, String content) {
        System.out.println(file);
        content.lines()
                .filter(line -> line.startsWith("import "))
                .forEach(line -> System.out.println("\t" + line));
    }

    @Test
    void ensureOnlyDtosAreUsed() {
        fileContents.forEach((file, contents) -> assertThat(contents.lines()
                .filter(line -> line.startsWith("import "))
                .filter(line -> line.contains("generated/de/spricom/zaster"))
                .filter(line -> !(line.contains("zaster/dtos") || line.contains("zaster/enums")))
        ).as(file).isEmpty());
    }

    @Test
    @Disabled
    void ensureOnlyStoresUseEndpoints() {
        fileContents.forEach((file, contents) -> {
            if (!file.startsWith("stores/")) {
                assertThat(contents.lines()
                        .filter(line -> line.startsWith("import "))
                        .filter(line -> line.contains("generated/endpoints"))
                ).as(file).isEmpty();
            }
        });
    }
}
