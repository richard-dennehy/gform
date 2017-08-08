package uk.gov.hmrc.gform

import java.nio.file.{Files, Paths}

import collection.JavaConverters._
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.ExecutionContext.Implicits.global

class UpsertAndGetFormTemplateSpec extends support.ITSpec {

   "upsert form template" in eventually {

       templates foreach { templateFile =>
        val template: JsValue = Json.parse(Files.newInputStream(templateFile))
        gformConnector.upsertTemplate(template).futureValue shouldBe (()) withClue templateFile.toString
      }
    }

  lazy val templates = Files
    .newDirectoryStream(Paths.get("sample-data"))
    .asScala
    .filter(Files.isRegularFile(_))
    .filter(_.getFileName.toString.matches("template-aa(.*)json")) //TODO fix other templates
    .toList

}
