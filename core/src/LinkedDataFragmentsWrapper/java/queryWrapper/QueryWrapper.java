package queryWrapper;

import org.linkeddatafragments.model.LinkedDataFragmentGraph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.query.*;

public class QueryWrapper {

    private Model model;

    public QueryWrapper() {
        LinkedDataFragmentGraph ldfg = new LinkedDataFragmentGraph("http://data.linkeddatafragments.org/dbpedia");
        model = ModelFactory.createModelForGraph(ldfg);
    }

    public ResultSet executeQuery(String queryString) {
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, model);

        return qe.execSelect();
    }

}
