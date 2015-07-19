package streams

import java.io._
import java.util
import java.util.Calendar

import com.hp.hpl.jena.datatypes.RDFDatatype
import com.hp.hpl.jena.graph.{Node, Triple, Graph}
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.shared.{Lock, Command, ReificationStyle, PrefixMapping}
import dataFormats.{WikiFusedResult, WikiListPage}
import org.apache.jena.riot.{RDFFormat, RDFDataMgr}

/**
 * Created by nico on 19/07/15.
 */
class RdfWriter {

  private def addStatement(s: Resource, p: Property, o: Resource) : Model = {
    val model = ModelFactory.createDefaultModel()
    model.add(s, p, o)
  }

  private def addMembershipStatement(listUri: String, entityUri: String, fileName: String) = {
    val subject = ResourceFactory.createResource(entityUri)
    val predicate = ResourceFactory.createProperty("dbpedia-lists", "memberOf")
    val rdfObject = ResourceFactory.createResource(listUri)
    val model = addStatement(subject, predicate, rdfObject)

    writeToFile(fileName, model)
  }

  private def addTypeStatement(entityUri: String, typeUri: String, fileName: String) = {
    val subject = ResourceFactory.createResource(entityUri)
    val predicate = ResourceFactory.createProperty("rdfs", "type")
    val rdfObject = ResourceFactory.createResource(typeUri)
    val model = addStatement(subject, predicate, rdfObject)

    println("==> Write to file")
    writeToFile(fileName, model)
  }

  def addMembershipStatementsFor(page: WikiListPage, fileName: String) = {
    page.listMembers foreach { entity =>
      addMembershipStatement(page.titleUri, entity.toUri, fileName: String)
    }
  }

  def addTypeStatementsFor(result: WikiFusedResult, fileName: String) = {
    // TODO: result.type is empty
    println("==| " + result.types)
    result.page.listMembers.foreach { entity =>
      result.types.foreach { typeUri =>
        addTypeStatement(entity.toUri, typeUri, fileName)
      }
    }
  }

  def writeToFile(fileName: String, model: Model) = {
    RDFDataMgr.write(new FileOutputStream(fileName, true), model, RDFFormat.TURTLE)
  }
}
