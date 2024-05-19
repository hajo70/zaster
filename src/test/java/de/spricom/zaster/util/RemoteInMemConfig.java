package de.spricom.zaster.util;

import org.h2.tools.Server;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.sql.SQLException;

@TestConfiguration
public class RemoteInMemConfig {

    // see https://www.baeldung.com/spring-boot-access-h2-database-multiple-apps
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server inMemoryH2DatabaseaServer() throws SQLException {
        return Server.createTcpServer(
                "-tcp", "-tcpAllowOthers", "-tcpPort", "9090");
    }
}
