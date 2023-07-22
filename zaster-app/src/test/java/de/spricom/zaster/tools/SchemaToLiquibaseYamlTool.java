package de.spricom.zaster.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.*;

@SpringBootTest
@ActiveProfiles("test")
public class SchemaToLiquibaseYamlTool {

    @Autowired
    private DataSource dataSource;

    @Test
    void dumpSchema() throws SQLException {
        Connection con = dataSource.getConnection();
        DatabaseMetaData metaData = con.getMetaData();
        System.out.println("=== Catalogs:");
        dump(metaData.getCatalogs());
        System.out.println("=== Schemas:");
        dump(metaData.getSchemas());
        System.out.println("=== Tables:");
        dump(metaData.getTables(null, "PUBLIC%", null, null));
        System.out.println("=== BOOKING Columns:");
        dump(metaData.getColumns(null, "PUBLIC%", "BOOKING", null));
        System.out.println("=== BOOKING Indexes:");
        dump(metaData.getIndexInfo(null, "PUBLIC", "BOOKING", false, false));
        System.out.println("=== BOOKING Imported Keys:");
        dump(metaData.getImportedKeys(null, "PUBLIC", "BOOKING"));
        System.out.println("=== BOOKING Exported Keys:");
        dump(metaData.getExportedKeys(null, "PUBLIC", "BOOKING"));
    }

    private void dump(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int count = metaData.getColumnCount();
        for (int i = 1; i <= count; i++) {
            System.out.print(metaData.getColumnName(i));
            if (i < count) {
                System.out.print("; ");
            }
        }
        System.out.println();
        while (rs.next()) {
            for (int i = 1; i <= count; i++) {
                System.out.print(rs.getString(i));
                if (i < count) {
                    System.out.print("; ");
                }
            }
            System.out.println();
        }
    }
}
