package tableExtraction;

import dataFormats.WikiLink;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RDFTable {
    TableRow headerRow;
    List<TableRow> rows_ = new ArrayList<>();

    public static int incomingRows;
    public static int usedRows;

    public RDFTable(List<TableRow> rows) {

        if (rows.size() == 0) {
            // throw new RuntimeException("table was empty");
        } else {
            headerRow = rows.get(0);
            rows_ = rows.subList(1,rows.size()).stream()
                    .map(this::getCleandedRow)
                    .filter(row -> row.getEntries().size() == headerRow.getEntries().size())
                    .collect(Collectors.toList());
        }
        incomingRows += rows.size();
        usedRows += rows_.size();
    }

    private TableRow getCleandedRow(TableRow row) {
        List<TableEntry> entries = row.getEntries().stream()
                .filter(entry -> entry.isLink() || !entry.getRawContent().equals(""))
                .collect(Collectors.toList());
        return new TableRow(entries);
    }

    public List<WikiLink> getColumnAsLinks(int columnIndex) {
        List<WikiLink> links = new ArrayList<>();
        for (TableRow row : rows_) {
            TableEntry entry = row.getEntries().get(columnIndex);
            if (entry.isLink()) {
                links.add(new WikiLink(entry.getRawContent(), entry.getLink()));
            }
        }
        return links;
    }

    public int getRowCount() {
        return rows_.size();
    }

    public TableEntry getElement(int rowIndex, int columnIndex) {
        return rows_.get(rowIndex).getEntries().get(columnIndex);
    }

    public int getColumnCount() {
        return headerRow.getEntries().size();
    }

    public List<String> getColumnAsRawString(int columnIndex) {
        return rows_.stream()
                    .map(row -> row.getEntries().get(columnIndex).getRawContent())
                    .collect(Collectors.toList());
    }

    public boolean columnContainsLinks(int columnIndex) {
        for (TableRow row : rows_) {
            TableEntry entry = row.getEntries().get(columnIndex);
            if (entry.isLink()) {
                return true;
            }
        }
        return false;
    }
}
