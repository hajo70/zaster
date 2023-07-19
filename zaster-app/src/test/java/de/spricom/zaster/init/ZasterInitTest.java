package de.spricom.zaster.init;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZoneId;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"init", "test"})
public class ZasterInitTest {

    @Autowired
    private ZasterInitProperties props;

    @Test
    void checkProperties() {
        assertThat(props).isNotNull();
        assertThat(props.getTenants()).hasSize(2);
        var me = props.getTenants().get("me");
        assertThat(me.getName()).isEqualTo("My personal Zaster");
        assertThat(me.getLocale()).isEqualTo(Locale.US);
        assertThat(me.getTimezone()).isEqualTo(ZoneId.of("GMT"));
        assertThat(me.getUsers()).hasSize(2)
                .containsKey("admin");
        assertThat(me.getUsers().get("admin").getName()).isEqualTo("Emma Powerful");
        assertThat(me.getUsers().get("admin").getRole()).isEqualTo(ZasterInitProperties.User.Role.ADMIN);

        assertThat(me.getIsoCurrencies()).contains("EUR", "USD");
        assertThat(me.getCurrencies()).isNotEmpty();
        var btc = me.getCurrencies().get("BTC");
        assertThat(btc.getName()).isEqualTo("Bitcoin");
        assertThat(btc.getType()).isEqualTo(ZasterInitProperties.Currency.Type.CRYPTO);

        assertThat(me.getAccounts()).isNotEmpty();
        var accounts = me.getAccounts().get("accounts");
        assertThat(accounts.getName()).isEqualTo("My bank accounts");
        assertThat(accounts.getAccounts()).isNotEmpty();
        assertThat(accounts.getAccounts().get("bank").getName()).isEqualTo("My bank");

        assertThat(me.getImports()).isNotEmpty();
        var imp = me.getImports().get("bank");
        assertThat(imp.getImporter()).isEqualTo("My bank CSV importer");
        assertThat(imp.getFiles()).hasSize(2);
        assertThat(imp.getFiles().get(0)).hasFileName("bank1.csv");
    }
}
