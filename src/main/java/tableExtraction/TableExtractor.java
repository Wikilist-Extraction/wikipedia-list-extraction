package tableExtraction;

import dataFormats.WikiLink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableExtractor {

    private String[][] tableAsString;

    public List<WikiLink> extractTableEntities(List<RDFTable> wikiTables) {
        List<WikiLink> links = new ArrayList<>();
        for (RDFTable table : wikiTables) {
            links.addAll(table.getColumnAsLinks(0));
        }
        return links;
    }

    private List<WikiLink> getSimpleEntities(RDFTable table) {
        return null;
    }

    private List<WikiLink> extractTableEntities(RDFTable table) {
        int[] rating = new int[table.getColumnCount()];
        TableRater rater = new TableRater();
        int[] uniquenessValues =  rater.getUniquenessValues(table);
        int[] leftnessValues = rater.rateLeftness(table);
        int[] columnValues = rater.rateColumnMatches(table);

        for (int i = 0; i < rating.length; i++) {
            rating[i] = uniquenessValues[i] + leftnessValues[i] + columnValues[i];
        }

        int maxColumn = getMax(rating);
        return table.getColumnAsLinks(maxColumn);
    }


    private void printStringTable(String[][] table) {
        for (String[] row : table) {
            System.out.println(Arrays.toString(row));
        }
    }


    private int getMax(int[] values) {
        int max = Integer.MIN_VALUE;
        int maxColumn = 0;
        for (int i = 0; i < values.length; i++) {
            if (values[i] > max){
                max = values[i];
                maxColumn = i;
            }
        }
        return maxColumn;
    }
}
