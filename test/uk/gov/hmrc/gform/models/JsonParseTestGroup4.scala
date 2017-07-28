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

package uk.gov.hmrc.gform.models

import play.api.libs.json._
import uk.gov.hmrc.gform.Spec
import uk.gov.hmrc.gform.core.Opt
import uk.gov.hmrc.gform.core.parsers.PresentationHintParser
import uk.gov.hmrc.gform.exceptions.{ InvalidState, UnexpectedState }

class JsonParseTestGroup4 extends Spec {

  "A raw group" should "fail to parse if repeatsMin/Max has errors" in {

    val jsonStr =
      """
      {
        "id": "gid",
        "label": "glabel"
        ,"presentationHint" : 123

      }
    """

    //     val res4: JsResult[Option[String]] = (Json.parse(jsonStr) \ "presentationHint").validateOpt[String]
    val res4: JsResult[Option[String]] = (Json.parse(jsonStr.replaceAll(""","presentationHint" : 123""", """""")) \ "presentationHint").validateOpt[String]
    //     val res4: JsResult[Option[String]] = (Json.parse(jsonStr.replaceAll("""123""", """"collapseGroupUnderLabel"""")) \ "presentationHint").validateOpt[String]
    //     val res4: JsResult[Option[String]] = (Json.parse(jsonStr.replaceAll("""123""", """"collapseGroupUnderLabe"""")) \ "presentationHint").validateOpt[String]
    //     val res4: JsResult[Option[String]] = (Json.parse(jsonStr.replaceAll("""123""", """"anything"""")) \ "presentationHint").validateOpt[String]
    //     val res4: JsResult[Option[String]] = (Json.parse(jsonStr.replaceAll("""123""", """{}""")) \ "presentationHint").validateOpt[String]

    def f(jsonStr: String): JsResult[Option[String]] = {

      (Json.parse(jsonStr) \ "presentationHint").validateOpt[String]

    }

    def jsResultToOpt[A](result: JsResult[A]): Opt[A] = {
      result match {
        case JsSuccess(a, _) => Right(a)
        case JsError(errors) => Left(InvalidState(errors.map {
          case (path, validationErrors) =>
            s"Path: ${path.toString}, Errors: ${validationErrors.map(_.messages.mkString(",")).mkString(",")}"
        }.mkString(",")))
      }
    }

    val res5: Opt[Option[String]] = jsResultToOpt(res4)

    println(res5)

    import cats.implicits._

    val res6: Opt[Option[PresentationHintExpr]] = res5.right.flatMap(optStr => optStr match {
      case Some(str) => {
        val res: Option[Opt[PresentationHintExpr]] = Some(PresentationHintParser.validate(str))
        val res2: Opt[Option[PresentationHintExpr]] = res.sequenceU
        res2
      }
      case None => Right(None)
    })

    println(res6)

    val res61: Opt[Option[PresentationHintExpr]] = res5.right.flatMap(optStr => optStr match {
      case Some(str) => optStr.map(PresentationHintParser.validate).sequenceU
      case None => Right(None)
    })

    println("res61 " + res61)

    val res7 = for {
      optStr <- res5.right
    } yield optStr match {
      case Some(str) => {
        val res: Option[Opt[PresentationHintExpr]] = optStr.map(PresentationHintParser.validate)
        val res2: Opt[Option[PresentationHintExpr]] = res.sequenceU
        res2
      }
      case None => Right(None)
    }

    println("res7 " + res7)

    val res8: Either[UnexpectedState, Opt[Option[PresentationHintExpr]]] = for {
      optStr <- res5.right
    } yield optStr.map(PresentationHintParser.validate).sequenceU

    println("res8 " + res8)

    val res9 = for {
      optStr <- res5.right
    } yield optStr match {
      case Some(str) => optStr.map(PresentationHintParser.validate).sequenceU
      case None => Right(None)
    }

    println(res9)

    val res10 = for {
      optStr <- res5.right
      res <- optStr.map(PresentationHintParser.validate).sequenceU
    } yield res

    println(res10)

    //     jsResultToOpt(f(jsonStr.replaceAll(""","presentationHint" : 123""", """""")))

  }

}
