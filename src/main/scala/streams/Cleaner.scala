package streams

import com.hp.hpl.jena.ontology.Ontology
import com.hp.hpl.jena.query.QuerySolution
import fragmentsWrapper.QueryWrapper
import sparql.JenaFragmentsWrapper

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.Success
/*
object Cleaner extends JenaFragmentsWrapper {
  private def buildOntologyTree(): OntologyNode = {
    var root = new OntologyNode("owl:Thing", null, Seq.empty[OntologyNode])
    var ontologyClasses = mutable.Queue(root)
    while (ontologyClasses.nonEmpty) {
      val currentClass = ontologyClasses.dequeue()

    }
    root
  }

  def getSubclasses(resource:String): List[String] = {
    val queryString = "SELECT ?subclass rdfs:subClassOf* " + resource
    val qexec:Future[List[QuerySolution]]  = queryFragmentWithUri(queryString, "http://linkeddatafragments.org/")

    qexec onSuccess  {
      case solutions => val resources = for (solution <- solutions) yield solution.toString
      return resources
    }

  }

  override val fragmentServerUrl: String = "http://linkeddatafragments.org/"
}

class OntologyNode(val data:String, val parent:OntologyNode,val children:Seq[OntologyNode]) {
  var _children = children
  var _parent = parent

  def getChildren:Seq[OntologyNode] = {
    _children
  }
  def addChild(child: OntologyNode) = {
    _children = _children :+ child
  }
  def getParent:OntologyNode = {
    _parent
  }
}

class Cleaner {

}
*/