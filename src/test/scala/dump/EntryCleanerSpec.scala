package dump

/**
 * Created by sven on 28/06/15.
 */

import dataFormats.Literal
import org.scalatest._

class EntryCleanerSpec extends FlatSpec {

    val entry = new {} with EntryCleaner {}

    "TEMPLATE string" should "be extracted to its wrapped value" in {
        assert(entry.extractTemplateValue("TEMPLATE[name, value]") == "value")
        assert(entry.extractTemplateValue("TEMPLATE[name, value1, value2, value3]") == "value1")
    }

    "Non-matching TEMPLATE string" should "return itself" in {
        assert(entry.extractTemplateValue("some value") == "some value")
        assert(entry.extractTemplateValue("TEMPLATE[]") == "TEMPLATE[]")
        assert(entry.extractTemplateValue("TEMPLATE[name, ]") == "TEMPLATE[name, ]")
    }

    "Extracting a Litaral" should "return a Literal" in {
        val literal = Literal("TEMPLATE[name, value]", "string")
        val computedLiteral = Literal("value", "string")
        assert(entry.extractTemplateFrom(literal) == computedLiteral)
    }
}
