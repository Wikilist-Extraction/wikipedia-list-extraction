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

  private def addStatement(s: Resource, p: Property, o: Resource, model: Model) = {
    model.add(s, p, o)
  }

  private def addMembershipStatement(listUri: String, entityUri: String, fileName: String, model: Model) = {
    val subject = ResourceFactory.createResource(entityUri)
    val predicate = ResourceFactory.createProperty("dbpedia-lists", "memberOf")
    val rdfObject = ResourceFactory.createResource(listUri)
    addStatement(subject, predicate, rdfObject, model)
  }

  private def addTypeStatement(entityUri: String, typeUri: String, fileName: String, model: Model) = {
    val subject = ResourceFactory.createResource(entityUri)
    val predicate = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type")
    val rdfObject = ResourceFactory.createResource(typeUri)
    addStatement(subject, predicate, rdfObject, model)
  }

  def addMembershipStatementsFor(page: WikiListPage, fileName: String) = {
    val model = ModelFactory.createDefaultModel()
    page.listMembers foreach { entity =>
      addMembershipStatement(page.titleUri, entity.toUri, fileName: String, model)
    }
    writeToFile(fileName, model)
  }

  def addTypeStatementsFor(result: WikiFusedResult, fileName: String) = {
    val model = ModelFactory.createDefaultModel()
    result.page.listMembers.foreach { entity =>
      result.types.foreach { typeUri =>
        addTypeStatement(entity.toUri, typeUri, fileName, model)
      }
    }
    writeToFile(fileName, model)
  }

  def writeToFile(fileName: String, model: Model) = {
    RDFDataMgr.write(new FileOutputStream(fileName, true), model, RDFFormat.NT)
  }
}
