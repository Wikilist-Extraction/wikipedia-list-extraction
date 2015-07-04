package tableExtraction;

import com.hp.hpl.jena.query.ResultSet;
import fragmentsWrapper.QueryWrapper;

import java.util.ArrayList;
import java.util.List;

public class PredicateMatcher {
    private final int MATCHING_RATIO = 3;

    public int countMatchingColumns(int columnIndex, RDFTable table) {
        int totalMatchingColumns = 0;
        int rowCount = table.getRowCount();

        if (!table.columnContainsLinks(columnIndex)) {
            return 0;
        }

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == columnIndex) {
                continue;
            }

            int matches = matchColumns(columnIndex, i, table);
            if (matches > rowCount / MATCHING_RATIO) {
                totalMatchingColumns++;
            }
        }
        return totalMatchingColumns;
    }

    private int matchColumns(int firstColumn, int secondColumn, RDFTable table) {
            int rowCount = table.getRowCount();

            int matchedEntries = 0;

            for (int i = 0; i < rowCount; i++) {
                TableEntry entryOfFirstColumn = table.getElement(i, firstColumn);
                TableEntry entryOfSecondColumn = table.getElement(i, secondColumn);

                if (predicatesExistBetween(entryOfFirstColumn, entryOfSecondColumn)) {
                    matchedEntries++;
                }
            }

            return matchedEntries;
    }

    private boolean predicatesExistBetween(TableEntry entryOfFirstColumn, TableEntry
            entryOfSecondColumn) {
        List<String> predicates = new ArrayList<>();
        if (entryOfFirstColumn.isDbpediaEntity() && entryOfSecondColumn.isDbpediaEntity() && !entryOfSecondColumn.isLink()) {
            predicates = getPredicatesBetweenEntities(entryOfFirstColumn,entryOfSecondColumn);
            predicates.addAll(predicatesBetweenEntityAndLiteral(entryOfFirstColumn, entryOfSecondColumn));
        }
        else if (entryOfFirstColumn.isDbpediaEntity() && entryOfSecondColumn.isDbpediaEntity()) {
            predicates = getPredicatesBetweenEntities(entryOfFirstColumn, entryOfSecondColumn);
        }
        else if (entryOfFirstColumn.isDbpediaEntity() && !entryOfSecondColumn.isDbpediaEntity())    {
            predicates = predicatesBetweenEntityAndLiteral(entryOfFirstColumn, entryOfSecondColumn);
        }
        else if (entryOfSecondColumn.isDbpediaEntity() &&  !entryOfFirstColumn.isDbpediaEntity()){
            predicates = predicatesBetweenEntityAndLiteral(entryOfSecondColumn, entryOfFirstColumn);
        }
        return predicates.size() != 0;
    }

    private List<String> getPredicatesBetweenEntities(TableEntry entryOfFirstColumn, TableEntry entryOfSecondColumn) {
        QueryWrapper helper = new QueryWrapper();

        ResultSet predicates;
        String name1;
        String name2;

        //TODO add redirections
        //if (entryOfFirstColumn.isLink()){
        //    name1 = helper.getRedirectionQuery(entryOfFirstColumn);
        //}

        name1 = entryOfFirstColumn.getLink();

        //if (entryOfSecondColumn.isLink()){
        //    name2 = helper.getRedirectedStringIfNeeded(entryOfSecondColumn);
        //}
        name2 = entryOfSecondColumn.getLink();


        predicates = helper.buildPredicateBetweenEntitiesQuery(name1, name2);
        return new ArrayList<>();
    }
    private List<String> predicatesBetweenEntityAndLiteral(TableEntry entry, TableEntry
            literalEntry) {

        ResultSet predicates;
        QueryWrapper helper = new QueryWrapper();
        String literal = literalEntry.getRawContent();
        //String name = helper.getRedirectedStringIfNeeded(entry);
        String name = entry.getLink();
        predicates = helper.buildPredicateBetweenEntityAndLiteral(name, literal);
        return new ArrayList<>(); 
    }
    /*

    public void findMatchingPredicates(RDFTable table) {
        int rowCount = table.getRowCount();
        List<String> tableNames = table.getColumnNames();
        System.out.println(table.getColumnRedirects(0));
        int matches = matchColumns(0, 3, table);
        //System.out.println(matches);
        /*for (int i = 0; i < table.getColumnCount() -1; i++) {
            for (int j = i + 1; j < table.getColumnCount(); j++) {
                int matchedCount = matchColumns(i, j, table);
                float percentage = matchedCount/rowCount;
                if (percentage > 0.3) {
                    System.out.print("matched column " + tableNames.get(i) + " with  " + tableNames.get(j));
                    System.out.println("with percantage: " + percentage);
                }
            }
        }
        //for (int i = 1; i < table.getColumnCount(); i++) {
        //    int matchedCount = matchColumns(0, i, table);
        //}
    }

    */
}
