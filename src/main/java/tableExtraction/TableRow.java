package tableExtraction;

import java.util.List;

public class TableRow {
    List<TableEntry> tableEntries;

    TableRow(List<TableEntry> entries) {
        tableEntries = entries;
    };

    public List<TableEntry> getEntries() {
        return tableEntries;
    }
}
