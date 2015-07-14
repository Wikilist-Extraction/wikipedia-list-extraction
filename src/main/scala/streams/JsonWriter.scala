package streams

import java.io.FileWriter

import dataFormats.WikiListResult
import spray.json.{JsArray, JsNumber, JsObject, JsString}
import typesExtraction.TfIdfWorker

/**
 * Created by nico on 13/07/15.
 */

object JsonWriter {

  def encodeWikistyle(str: String): String = str.replace(' ', '_')


  def resultToJson(association: (String, List[String])): (String, JsArray) = {
    encodeWikistyle(association._1) -> JsArray((association._2 map (JsString(_))).toVector)
  }

  def createResultJson(result: Map[String, List[String]]): JsObject = {
    JsObject(
      "lists" -> JsObject(result map resultToJson)
    )
  }

  def tfIdfMapToJson(tfIdfMap: Map[String, Double]) = {
    JsObject(tfIdfMap.mapValues(JsNumber(_)))
  }

  def createTfIdfJson(results: List[WikiListResult]): JsObject = {

    val objects = results.map { result =>
      val tfIdfMap = result.scores.get(TfIdfWorker.testSymbol).get
      JsObject(
        "listId" -> JsString(encodeWikistyle(result.page.title)),
        "entityCount" -> JsNumber(result.page.listMembers.size),
        "results" -> JsArray( (result.getTypes map { typeName: String =>
          JsObject(
            "typeUri" -> JsString(typeName),
            "count" -> JsNumber(result.types.get(typeName).get),
            "tfIdf" -> JsNumber(tfIdfMap.get(typeName).get)
          )
        }).toVector
        )
      )
    }

    JsObject("lists" -> JsArray(objects.toVector))
  }

  def write(json: JsObject, fileName: String) = {
    val writer = new FileWriter("results/tfidf.json")
    writer.write(json.prettyPrint)
    writer.close()
  }
}
