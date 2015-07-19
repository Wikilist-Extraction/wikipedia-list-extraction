package tableExtraction;

import dataFormats.WikiLink;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RDFTable {
    TableRow headerRow;
    List<TableRow> rows_ = new ArrayList<>();

    public RDFTable(List<TableRow> rows) {
        headerRow = rows.get(0);
        rows_ = rows.subList(1,rows.size());
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

    public List<String> getColumnNames() {
        return headerRow.getEntries().stream().map(TableEntry::getRawContent)
                .collect(Collectors.toList());
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
    /*public List<Boolean> getColumnIsDBpediaEntry(int columnIndex) {
        List<Boolean> entryStatus = new ArrayList<>();
        for (TableEntry[] row : table) {
            TableEntry entry = row[columnIndex];
            entryStatus.add(entry.isDbpediaEntity());
            System.out.println(entry.isDbpediaEntity() + " " + entry.getRawContent());
        }
        return entryStatus;
    }




    public String[][] getTableAsTextArray() {
        int rowCount = table.size();
        String[][] textArray = new String[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                textArray[i][j] = table.get(i)[j].getRawContent();
            }
        }

        return textArray;
    }




    public List<String> getColumnAsRDF(int index) {
        List<String> columnAsRDF = new LinkedList<>();
        for (TableEntry[] row : table) {
            if (row.toString().contains("<th")) {
                //skip header row
                continue;
            }

            String rawString = row[index].getRawContent();
            String title = getTitleFromLink(rawString);

            columnAsRDF.add(title);
        }
        return columnAsRDF;
    }*/
    /*
    public List<String> getColumnRedirects(int columnIndex) {
        List<String> columnRedirects = new LinkedList<>();
        SPARQLHelper helper = new SPARQLHelper();
        for (TableEntry[] row : table) {
            if (row.toString().contains("<th")) {
                //skip header row
                continue;
            }

            TableEntry entry = row[columnIndex];
            helper.getRedirectedStringIfNeeded(entry);
            columnRedirects.add(helper.getRedirectedStringIfNeeded(entry));
        }
        return columnRedirects;
    }*/
    /*

    private String getTitleFromLink(String rawString) {
        if (rawString.contains("(page does not exist)")) {
            return "";
        }
        String title = StringUtils.substringBetween(rawString, "title=\"", "\"");
        if (title == null) {
            return "";
        }
        return title;
    }

    public void printTable() {
        int rowCount = table.size();
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                System.out.println(table.get(i)[j].getRawContent() + "  ");
            }
            System.out.println();
        }
    }
    */
}
