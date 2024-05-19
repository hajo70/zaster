package de.spricom.zaster.importing.csv;

public record CsvRow(
        String[] columns,
        String md5
) {
    public String column(int index) {
        if (index > columns.length) {
            return null;
        }
        return columns[index];
    }

    public String column(String indexChar) {
        return column(indexChar.charAt(0) - 'A');
    }

    public static String index(int i) {
        return "" + (char)('A' + i);
    }
}
