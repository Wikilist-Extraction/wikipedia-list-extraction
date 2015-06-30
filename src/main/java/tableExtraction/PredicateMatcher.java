package tableExtraction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PredicateMatcher {

    public int countMatchingColumns(int columnIndex, RDFTable table) {
        int totalMatchingColumns = 0;
        int rowCount = table.getRowCount();

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == columnIndex) continue;
            int matches = matchColumns(columnIndex, i, table);
            if (matches > rowCount / 3) {
                totalMatchingColumns++;
            }
        }
        return totalMatchingColumns;
    }

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
        }*/
        //for (int i = 1; i < table.getColumnCount(); i++) {
        //    int matchedCount = matchColumns(0, i, table);
        //}
    }

    private int matchColumns(int firstColumn, int secondColumn, RDFTable table) {
        int rowCount = table.getRowCount();

        int matchedEntries = 0;

        for (int i = 1; i < rowCount; i++) {
            TableEntry entryOfFirstColumn = table.getElement(i, firstColumn);
            TableEntry entryOfSecondColumn = table.getElement(i, secondColumn);

            List<String> predicates = new ArrayList<>();
            if (entryOfFirstColumn.isDbpediaEntity() && entryOfSecondColumn.isDbpediaEntity() && !entryOfSecondColumn.isLink()) {
                predicates = getPredicatesBetweenEntities(entryOfFirstColumn,entryOfSecondColumn);
                predicates.addAll(getPredicatesBetweenEntityAndLiteral(entryOfFirstColumn, entryOfSecondColumn));
            }
            else if (entryOfFirstColumn.isDbpediaEntity() && entryOfSecondColumn.isDbpediaEntity()) {
                predicates = getPredicatesBetweenEntities(entryOfFirstColumn, entryOfSecondColumn);
            }
            else if (entryOfFirstColumn.isDbpediaEntity() && !entryOfSecondColumn.isDbpediaEntity())    {
                predicates = getPredicatesBetweenEntityAndLiteral(entryOfFirstColumn, entryOfSecondColumn);
            }
            else if (entryOfSecondColumn.isDbpediaEntity() &&  !entryOfFirstColumn.isDbpediaEntity()){
                predicates = getPredicatesBetweenEntityAndLiteral(entryOfSecondColumn, entryOfFirstColumn);
            }
            else {
                return 0;
            }

            System.out.println(predicates.toString());
            if (predicates.size() != 0) {
                matchedEntries++;
            }
        }

        return matchedEntries;
    }
    private List<String> getPredicatesBetweenEntities(TableEntry entryOfFirstColumn, TableEntry entryOfSecondColumn) {
        SPARQLHelper helper = new SPARQLHelper();

        List<String> predicates = new LinkedList<>();
        String name1;
        String name2;
        if (entryOfFirstColumn.isLink()){
            name1 = helper.getRedirectedStringIfNeeded(entryOfFirstColumn);
        } else {
            name1 = entryOfFirstColumn.getTextContent();
        }
        if (entryOfSecondColumn.isLink()){
            name2 = helper.getRedirectedStringIfNeeded(entryOfSecondColumn);
        } else {
            name2 = entryOfSecondColumn.getTextContent();
        }

        predicates = helper.getPredicatesBetweenEntities(name1, name2);
        return predicates;
    }

    private List<String> getPredicatesBetweenEntityAndLiteral(TableEntry entry, TableEntry literalEntry) {
        SPARQLHelper helper = new SPARQLHelper();

        List<String> predicates = new LinkedList<>();
        String literal = literalEntry.getTextContent();
        String name = helper.getRedirectedStringIfNeeded(entry);

        predicates = helper.getPredicatesBetweenEntityAndLiteral(name, literal);
        System.out.println("Finding predicates between: " + name + " and " + literal);
        return predicates;
    }
}
