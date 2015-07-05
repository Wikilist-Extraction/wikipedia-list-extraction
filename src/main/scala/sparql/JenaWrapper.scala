package sparql

import com.hp.hpl.jena.query._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.shared.PrefixMapping
import org.linkeddatafragments.model.LinkedDataFragmentGraph

import scala.concurrent._
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by nico on 01/07/15.
 */
abstract class JenaWrapper {

  def createQuery(queryString: String): Query = QueryFactory.create(queryString)

  def createParameterizedQuery(queryString: String): ParameterizedSparqlString = {
    new ParameterizedSparqlString(queryString)
  }
  def addStandardPrefixes(pss: ParameterizedSparqlString): Unit = {
    pss.setNsPrefixes(PrefixMapping.Standard)
  }

  def execQuery(qexec: QueryExecution): Future[List[QuerySolution]] = {
    val futureResult: Future[ResultSet] = Future {
      blocking {
        qexec.execSelect()
      }
    }

    val resultList: Future[List[QuerySolution]] = futureResult map {result =>
      val list = ListBuffer[QuerySolution]()
      while(result.hasNext) {
        list += result.next()
      }
      list.toList
    }

    resultList
  }
}

trait JenaFragmentsWrapper extends JenaWrapper {

  val fragmentServerUrl: String

  lazy val fragmentGraph = new LinkedDataFragmentGraph(fragmentServerUrl)
  lazy val model = ModelFactory.createModelForGraph(fragmentGraph)


  def queryFragmentWithUri(queryString: String, uri: String): Future[List[QuerySolution]] = {
    val pss = createParameterizedQuery(queryString)
    pss.setIri("uri", uri)
    addStandardPrefixes(pss)
    execQuery(createFragmentQueryExecution(pss))
  }


  def createFragmentQueryExecution(query: Query): QueryExecution = QueryExecutionFactory.create(query, model)
  def createFragmentQueryExecution(pss: ParameterizedSparqlString): QueryExecution = createFragmentQueryExecution(pss.asQuery())
  def createFragmentQueryExecution(queryString: String): QueryExecution = {
    QueryExecutionFactory.create(createQuery(queryString), model)
  }
}

trait JenaSparqlWrapper extends JenaWrapper {

  val endpointUrl: String

  def querySparqlWithUri(queryString: String, uri: String): Future[List[QuerySolution]] = {
    val pss = createParameterizedQuery(queryString)
    pss.setIri("uri", uri)
    addStandardPrefixes(pss)
    execQuery(createSparqlQueryExecution(pss))
  }

  def createSparqlQueryExecution(query: Query): QueryExecution = QueryExecutionFactory.sparqlService(endpointUrl, query)
  def createSparqlQueryExecution(pss: ParameterizedSparqlString): QueryExecution = createSparqlQueryExecution(pss.asQuery())
  def createSparqlQueryExecution(queryString: String): QueryExecution = {
    QueryExecutionFactory.sparqlService(endpointUrl, createQuery(queryString))
  }
}
