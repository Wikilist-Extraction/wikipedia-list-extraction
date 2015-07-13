package sinks

import akka.stream.scaladsl.{Flow, Sink}
import dataFormats.WikiListResult
import spray.json.{JsString, JsArray, JsObject}

/**
 * Created by nico on 13/07/15.
 */


class JsonWriter {

  def resultToJson(association: (String, List[String])): (String, JsArray) = {
    association._1 -> JsArray((association._2 map (JsString(_))).toVector)
  }

  def createJson(result: Map[String, List[String]]): JsObject = {
    JsObject(
      "lists" -> JsObject(result map resultToJson)
    )
  }
}
