package streams

import java.io.FileWriter

import dataFormats.{WikiListResult, WikiListScores}
import ratings.TfIdfRating
import spray.json.{JsArray, JsNumber, JsObject, JsString}
import util.UriUtils

/**
 * Created by nico on 13/07/15.
 */

object JsonWriter {

//  def createResultJson(result: Map[String, List[String]]): JsObject = {
//    JsObject(
//      "lists" -> JsObject(result map resultToJson)
//    )
//  }

  def createResultJson(results: List[WikiListResult]): JsObject = {
    JsObject(
      "lists" -> JsObject(results.map { result =>
        UriUtils.encodeWikistyle(result.page.title) -> JsArray((result.types map (JsString(_))).toVector)
      })
    )
  }

  def tfIdfMapToJson(tfIdfMap: Map[String, Double]) = {
    JsObject(tfIdfMap.mapValues(JsNumber(_)))
  }

  def createTfIdfJson(results: List[WikiListScores]): JsObject = {

    val objects = results.map { result =>
      val tfIdfMap = result.scores.get(TfIdfRating.name).get
      JsObject(
        "listId" -> JsString(UriUtils.encodeWikistyle(result.page.title)),
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
    val writer = new FileWriter(fileName)
    writer.write(json.prettyPrint)
    writer.close()
  }
}
