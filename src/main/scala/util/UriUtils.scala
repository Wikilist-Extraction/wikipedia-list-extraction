package util

import spray.json.{JsString, JsArray}

/**
 * Created by nico on 19/07/15.
 */
object UriUtils {
  def encodeWikistyle(str: String): String = str.replace(' ', '_')
}
