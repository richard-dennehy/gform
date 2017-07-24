/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.gform.services

import play.mvc.Http.{ HeaderNames, MimeTypes }
import uk.gov.hmrc.gform.connectors.PDFGeneratorConnector
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

object PDFGeneratorService extends PDFGeneratorService {
  lazy val pdfConnector = PDFGeneratorConnector
}

trait PDFGeneratorService {

  def pdfConnector: PDFGeneratorConnector

  def generatePDF(html: String)(implicit hc: HeaderCarrier): Future[Array[Byte]] = {
    val headers = Seq((HeaderNames.CONTENT_TYPE, MimeTypes.FORM))
    val body = Map("html" -> Seq(html))
    pdfConnector.generatePDF(body, headers)
  }
}