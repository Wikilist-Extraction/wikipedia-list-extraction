package tableExtraction;

import org.slf4j.Logger;
import dataFormats.WikiLink;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableExtractor {

    public static int tableEntitiesCount = 0;
    public static int overallGivenRows = 0;

    private Logger logger = LoggerFactory.getLogger("TableExtractor");

    public List<WikiLink> extractTableEntities(List<RDFTable> wikiTables) {
        List<WikiLink> links = wikiTables.stream()
                .map(this::extractSingleTable)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        tableEntitiesCount += links.size();
        return links;
    }

    private List<WikiLink> extractSingleTable(RDFTable table) {
        overallGivenRows += table.getRowCount();

        if (table.getRowCount() == 0) {
            logger.info("empty table was given");
            return new ArrayList<>();
        }

        int[] ratedColumns = rateColumns(table);


        int maxColumn = getMaxIndex(ratedColumns);
        return table.getColumnAsLinks(maxColumn);
    }

    private int getMaxIndex(int[] values) {
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

    private int[] rateColumns(RDFTable table) {
        TableRater rater = new TableRater();

        int[] rating = new int[table.getColumnCount()];

        int[] uniqueness = rater.getUniquenessValues(table);
        int[] leftness =  rater.rateLeftness(table);
        int[] matching = rater.rateColumnMatches(table);

        for (int i = 0; i < rating.length; i++) {
            rating[i] = uniqueness[i] + leftness[i] + matching[i];
        }

        return rating;
    }
}
