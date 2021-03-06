package fragmentsWrapper;


import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.tdb.TDBFactory;
import org.apache.commons.lang.StringUtils;
import org.linkeddatafragments.model.LinkedDataFragmentGraph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.query.*;

public class QueryWrapper {

    private Model model;

    public static int numberOfQueries = 0;

    private String prefixes = "" +
        "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> " +
        "PREFIX dbpedia: <http://dbpedia.org/resource/> " +
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>" ;

    public QueryWrapper() {
        String titleTdbDirectory = "db/properties";
        Dataset propertyDataset = TDBFactory.createDataset(titleTdbDirectory);
        model = propertyDataset.getDefaultModel();
    }

    /*
    public ResultSet executeQueryFragments(String queryString) {
        Query query = QueryFactory.create(prefixes + queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, linkedDataModel);
        //QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

        return qexec.execSelect();
    }*/

    public ResultSet executeQuery(String queryString) {

        Query query = QueryFactory.create(prefixes + queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);

        numberOfQueries++;

        return qe.execSelect();
    }

    public ResultSet getTypes(String uri) {
        String queryString = "select ?type from <http://dbpedia.org> where { <" + uri + "> rdf:type ?type }";
        return executeQuery(queryString);
    }

    public ResultSet buildPredicateBetweenEntitiesQuery(String name1, String name2) {
        String queryString = "SELECT ?predicate FROM <http://dbpedia.org> WHERE { " +
            "<http://dbpedia.org/resource/" + name1 + "> ?predicate <http://dbpedia.org/resource/" + name2  + ">" + ". }";
        return executeQuery(queryString);
    }

    public ResultSet buildPredicateBetweenEntityAndLiteral(String entityName, String literal) {
        if (!StringUtils.isNumeric(literal))
            literal = "\"" + literal + "\"" + "@en";

        String queryString = "SELECT ?predicate { <http://dbpedia.org/resource/" + entityName + "> ?predicate "  + literal +". }";
        return executeQuery(queryString);
    }

    public ResultSet getRedirectionQuery(String name) {
        String queryString = "SELECT ?object {" +
            "<http://dbpedia.org/resource/" + name + "> <http://dbpedia.org/ontology/wikiPageRedirects> ?object. } ";
        return executeQuery(queryString);
    }

    public ResultSet getSimpleString() {
        String queryString =  "SELECT ?s WHERE {?s ?o ?p.} LIMIT 5";
        return executeQuery(queryString);
    }

    public ResultSet buildPredicateQuery(String name) {
        String queryString = "SELECT ?predicate { <http://dbpedia.org/resource/" + name + "> ?predicate ?object. }";
        return executeQuery(queryString);
    }

    public boolean isLiteralEntityQueryString(String name) {
        name = name.replace(" ", "_");
        String queryString = "SELECT * {{" +
            "SELECT ?predicate { <http://dbpedia.org/resource/" + name + "> ?predicate ?object. }}" +
            "UNION { SELECT ?subject " +
            "{ ?subject ?predicate <http://dbpedia.org/resource/" + name +">}} }";
        ResultSet predicates = null;
        try {
            predicates = executeQuery(queryString);
        } catch (Exception e){
            return false;
        }
        return predicates.hasNext();
    }
}
