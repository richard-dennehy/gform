/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.gform.formtemplate

import uk.gov.hmrc.gform.sharedmodel.form.FormField
import uk.gov.hmrc.gform.sharedmodel.formtemplate._

import scala.collection.immutable.List

object SectionHelper {

  def atomicFields(section: BaseSection, data: Map[FormComponentId, FormField]): List[FormComponent] = atomicFields(section.fields, data)

  private def atomicFields(fieldValues: List[FormComponent], data: Map[FormComponentId, FormField]): List[FormComponent] = {
    fieldValues.flatMap { (fieldValue) =>
      fieldValue.`type` match {
        case Group(gfvs, _, repMax, _, _, _) => atomicFields(fixLabels(gfvs), data) ++ findIdsInRepeatingGroup(gfvs, repMax, data)
        case fv @ _ => List(fieldValue)
      }
    }
  }

  private def findIdsInRepeatingGroup(fields: List[FormComponent], repeatsMax: Option[Int], data: Map[FormComponentId, FormField]): List[FormComponent] = {

    val result = if (data.isEmpty) {
      Nil // no data, no way of knowing if we have repeating groups
    } else {
      repeatsMax.map(extractRepeatingGroupFieldIds(fields, _, data)).getOrElse(Nil)
    }

    atomicFields(result, data)
  }

  private def extractRepeatingGroupFieldIds(fields: List[FormComponent], repeatsMax: Int, data: Map[FormComponentId, FormField]): List[FormComponent] = {
    (1 until repeatsMax).map { i =>
      fields.flatMap { fieldInGroup =>
        data.keys.flatMap { key =>
          val fieldName = s"${i}_${fieldInGroup.id.value}"
          key.value.startsWith(fieldName) match {
            case true => List(fieldInGroup.copy(
              id = FormComponentId(fieldName),
              label = buildRepeatingText(Some(fieldInGroup.label), i + 1).getOrElse(""),
              shortName = buildRepeatingText(fieldInGroup.shortName, i + 1)))
            case false => Nil
          }
        }.toSet
      }
    }.toList.flatten
  }

  private def fixLabels(fieldValues: List[FormComponent]): List[FormComponent] = {
    fieldValues.map { field =>
      if (field.label.contains("$n") || (field.shortName.isDefined && field.shortName.get.contains("$n"))) {
        field.copy(
          label = buildRepeatingText(Some(field.label), 1).get,
          shortName = buildRepeatingText(field.shortName, 1))
      } else {
        field
      }
    }
  }

  private def buildRepeatingText(text: Option[String], index: Int) = text match {
    case Some(txt) if text.get.contains("$n") => Some(txt.replace("$n", index.toString))
    case _ => text
  }

}
