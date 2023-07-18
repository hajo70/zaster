package de.spricom.zaster.init;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "zaster.init")
@Data
public class ZasterInitProperties {

    private Map<String, Tenant> tenants;

    @Data
    static class Tenant {
        private String name;
        private Locale locale;
        private ZoneId timezone;
        private Map<String, User> users;
        private List<String> isoCurrencies;
        private Map<String, Currency> currencies;
        private Map<String, Account> accounts;
        private Map<String, Import> imports;
    }


    @Data
    static class User {
        enum Role {USER, ADMIN}

        private String name;
        private Role role;
    }

    @Data
    static class Currency {
        enum Type {CRYPTO, FIAT, METAL}

        private String name;
        private Type type;
    }

    @Data
    static class Account {
        private String name;
        private String code;
        private Map<String, Account> accounts;
    }

    @Data
    static class Import {
        private String importer;
        private List<File> files;
    }
}
