package de.spricom.zaster.importing.csv;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

@Getter
@AllArgsConstructor
public class CsvReader {
    private final String delimiter;
    private final Charset charset;

    public List<CsvRow> scan(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, charset))) {
            return br.lines().map(this::toRow).toList();
        }
    }

    private CsvRow toRow(String row) {
        return new CsvRow(row.split(delimiter), md5Hash(row));
    }

    private String md5Hash(String row) {
        return DigestUtils.md5Hex(row);
    }
}
