package de.spricom.zaster.importers;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Getter
@Setter
public class CsvReader {
    private String delimitator = ";";
    private Charset encoding = StandardCharsets.UTF_8;

    public List<CsvRow> scan(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding))) {
            return br.lines().map(this::toRow).toList();
        }
    }

    private CsvRow toRow(String row) {
        return new CsvRow(
            row.split(delimitator),
            md5Hash(row)
        );
    }

    private String md5Hash(String row) {
        return DigestUtils.md5Hex(row);
    }
}
