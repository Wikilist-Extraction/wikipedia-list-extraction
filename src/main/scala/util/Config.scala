package util

import com.typesafe.config.ConfigFactory

/**
 * Created by nico on 28/08/15.
 */
object Config {
  val config = ConfigFactory.load()
}
