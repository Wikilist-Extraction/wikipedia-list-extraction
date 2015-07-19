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
  val model = ModelFactory.createDefaultModel()


  private def addStatement(s: Resource, p: Property, o: Resource) = model.add(s, p, o)

  private def addMembershipStatement(listUri: String, entityUri: String) = {
    val subject = ResourceFactory.createResource(entityUri)
    val predicate = ResourceFactory.createProperty("dbpedia-lists", "memberOf")
    val rdfObject = ResourceFactory.createResource(listUri)
    addStatement(subject, predicate, rdfObject)
  }

  private def addTypeStatement(entityUri: String, typeUri: String) = {
    val subject = ResourceFactory.createResource(entityUri)
    val predicate = ResourceFactory.createProperty("rdfs", "type")
    val rdfObject = ResourceFactory.createResource(typeUri)
    addStatement(subject, predicate, rdfObject)
  }

  def addMembershipStatementsFor(page: WikiListPage) = {
    println("here add statements")
    page.listMembers foreach { entity =>
      addMembershipStatement(page.titleUri, entity.toUri)
    }
  }

  def addTypeStatementsFor(result: WikiFusedResult) = {
    result.page.listMembers.foreach { entity =>
      result.types.foreach { typeUri =>
        addTypeStatement(entity.toUri, typeUri)
      }
    }
  }

  def writeToFile(fileName: String) = {
    RDFDataMgr.write(new FileOutputStream(fileName), model, RDFFormat.TURTLE)
  }
}
