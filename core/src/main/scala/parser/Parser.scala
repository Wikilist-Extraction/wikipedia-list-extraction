package parser

import it.cnr.isti.hpc.wikipedia.article.{Link, Table}

/**
 * Created by sven on 28/06/15.
 */
class Parser(
            _links: List[Link],
            _externalLinks: List[Link]
              )  {

  val links: List[Link] = _links
  val externalLinks: List[Link] = _externalLinks

  def parseLists(lists: List[List[String]]): List[List[String]] = { lists }

  def parseTables(tables: List[Table]): List[Table] = { tables }
    // loop tables
    // parse each
    // add to new list
    // return list
}
