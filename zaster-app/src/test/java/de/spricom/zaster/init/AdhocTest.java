package de.spricom.zaster.init;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;

public class AdhocTest {

    @Test
    void dumpZoneId() {
        System.out.println(ZoneId.systemDefault().getId());
    }
}
