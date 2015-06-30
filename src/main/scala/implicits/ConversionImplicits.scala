package implicits

import scala.collection.JavaConverters._
import scala.language.implicitConversions


/**
 * Implicit conversions from java lists to scala lists
 */
object ConversionImplicits {

  implicit def asScalaList[T](list: java.util.List[T]): List[T] = {
    list.asScala.toList
  }

  implicit def asScalaListOfLists[T](list: java.util.List[java.util.List[T]]): List[List[T]] = {
    list.asScala.toList map { _.asScala.toList }
  }

  implicit def asJavaList[T](list: List[T]):  java.util.List[T] = {
    list.asJava
  }

}
