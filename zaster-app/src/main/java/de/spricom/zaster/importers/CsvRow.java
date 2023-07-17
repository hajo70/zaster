package de.spricom.zaster.importers;

public record CsvRow(
        String[] columns,
        String md5
) {
    String column(int index) {
        if (index > columns.length) {
            return null;
        }
        return columns[index];
    }

    String column(String indexChar) {
        return column(indexChar.charAt(0) - 'A');
    }

    static String index(int i) {
        return "" + (char)('A' + i);
    }
}
