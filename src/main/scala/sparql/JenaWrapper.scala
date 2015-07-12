package sparql

import com.hp.hpl.jena.query._
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.shared.{Lock, PrefixMapping}
import com.hp.hpl.jena.tdb.TDBFactory
import org.apache.jena.riot.RDFDataMgr
import org.linkeddatafragments.model.LinkedDataFragmentGraph

import scala.concurrent._
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Created by nico on 01/07/15.
 */
abstract class JenaWrapper {

  val DBpediaPrefixes = PrefixMapping.Factory.create()
    .setNsPrefixes( PrefixMapping.Standard )
    .setNsPrefix("dbpedia-owl", "http://dbpedia.org/ontology/")
    .setNsPrefix("dbpedia", "http://dbpedia.org/resource/")
    .lock()

  def createQuery(queryString: String): Query = QueryFactory.create(queryString)

  def createParameterizedQuery(queryString: String): ParameterizedSparqlString = {
    new ParameterizedSparqlString(queryString)
  }
  def addStandardPrefixes(pss: ParameterizedSparqlString): Unit = {
    pss.setNsPrefixes(DBpediaPrefixes)
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

trait JenaDumpWrapper extends JenaWrapper {
  val tdbDirectory: String

  lazy val dataset = TDBFactory.createDataset(tdbDirectory)
  lazy val model = dataset.getDefaultModel

  def queryDumpWithUri(queryString: String, uri: String): Future[List[QuerySolution]] = {
    val pss = createParameterizedQuery(queryString)
    pss.setIri("uri", uri)
    addStandardPrefixes(pss)
    execQuery(createDumpQueryExecution(pss))
  }

  def createDumpQueryExecution(query: Query): QueryExecution = QueryExecutionFactory.create(query, model)
  def createDumpQueryExecution(pss: ParameterizedSparqlString): QueryExecution = createDumpQueryExecution(pss.asQuery())
  def createDumpQueryExecution(queryString: String): QueryExecution = {
    QueryExecutionFactory.create(createQuery(queryString), model)
  }

}


//trait JenaDumpWrapper extends JenaWrapper {
//  val dbpediaTypesDumpFileName: String
////  val yagoTypesDumpFileName: String
//
//  lazy val dbpediaTypesModel = RDFDataMgr.loadModel(dbpediaTypesDumpFileName)
////  lazy val yagoTypesModel = RDFDataMgr.loadModel(yagoTypesDumpFileName)
//
////  lazy val model = dbpediaTypesModel.add(yagoTypesModel)
//  lazy val model = dbpediaTypesModel
//  def queryDumpWithUri(queryString: String, uri: String): Future[List[QuerySolution]] = {
//    val pss = createParameterizedQuery(queryString)
//    pss.setIri("uri", uri)
//    addStandardPrefixes(pss)
//    execQuery(createDumpQueryExecution(pss))
//  }
//
//  def createDumpQueryExecution(query: Query): QueryExecution = QueryExecutionFactory.create(query, model)
//  def createDumpQueryExecution(pss: ParameterizedSparqlString): QueryExecution = createDumpQueryExecution(pss.asQuery())
//  def createDumpQueryExecution(queryString: String): QueryExecution = {
//    QueryExecutionFactory.create(createQuery(queryString), model)
//  }
//
//}
