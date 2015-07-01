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
trait JenaFragmentWrapper {

  val fragmentServerUrl: String
  lazy val fragmentGraph = new LinkedDataFragmentGraph(fragmentServerUrl)
  lazy val model = ModelFactory.createModelForGraph(fragmentGraph)


  def queryWithUri(queryString: String, uri: String): Future[List[QuerySolution]] = {
    val pss = createParameterizedQuery(queryString)
    pss.setIri("uri", uri)
    addStandardPrefixes(pss)
    execQuery(createQueryExecution(pss))
  }

  def createQuery(queryString: String): Query = QueryFactory.create(queryString)

  def createQueryExecution(query: Query): QueryExecution = QueryExecutionFactory.create(query, model)
  def createQueryExecution(pss: ParameterizedSparqlString): QueryExecution = createQueryExecution(pss.asQuery())
  def createQueryExecution(queryString: String): QueryExecution = {
    QueryExecutionFactory.create(createQuery(queryString), model)
  }

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

