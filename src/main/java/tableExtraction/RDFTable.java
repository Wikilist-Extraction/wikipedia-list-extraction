package tableExtraction;

import dataFormats.WikiLink;

import java.util.ArrayList;
import java.util.List;

public class RDFTable {
    //ArrayList<TableEntry[]> table = new ArrayList<>();
    private int colCount;
    private int rowCount = 0;
    List<TableRow> rows_ = new ArrayList<>();

    RDFTable(List<TableRow> rows) {
        rows_ = rows;
    };

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

    /*public void insertRow(TableEntry[] row) {
        if (row.length != colCount) {
            //TODO throw useful exception
            System.out.println("wrong number of columns");
        }
        rowCount++;
        table.add(row);
    }*/

    /*public List<Boolean> getColumnIsDBpediaEntry(int columnIndex) {
        List<Boolean> entryStatus = new ArrayList<>();
        for (TableEntry[] row : table) {
            TableEntry entry = row[columnIndex];
            entryStatus.add(entry.isDbpediaEntity());
            System.out.println(entry.isDbpediaEntity() + " " + entry.getRawContent());
        }
        return entryStatus;
    }

    public int getRowCount() {
        return rowCount;
    }

    public List<String> getColumnNames() {
        List<String> columnNames = new LinkedList<>();
        TableEntry[] firstRow = table.get(0);
        for (TableEntry entry : firstRow) {
            columnNames.add(entry.getRawContent());
        }
        return columnNames;
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

    public TableEntry getElement(int i, int j) {
        return table.get(i)[j];
    }

    public int getColumnCount() {
        return colCount;
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
    public List<String> getColumnAsRawString(int columnIndex) {
        List<String> columnAsRawString = new LinkedList<>();
        for (TableEntry[] row : table) {
            //skip header row
            if (Arrays.toString(row).contains("<th")) {
                continue;
            }

            String rawString = row[columnIndex].getRawContent();
            columnAsRawString.add(rawString);
        }

        return columnAsRawString;
    }

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
