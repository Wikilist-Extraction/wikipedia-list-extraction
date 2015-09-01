package filtering

import dataFormats.{WikiFusedResult, WikiListResult}
import filtering.strategies.ScoreFilterStrategy

/**
 * Created by nico on 28/08/15.
 */
class ScoreFilter(filterStrategy: ScoreFilterStrategy) {

  def filter(result: WikiFusedResult): WikiListResult = {

    val filteredTypes = filterStrategy.filterScores(result)

    WikiListResult(result.wikiListScores.page, filteredTypes)
  }
}
