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

package uk.gov.hmrc.gform.formtemplate

import cats.Monoid
import uk.gov.hmrc.gform.core.{ Invalid, Opt, Valid, ValidationResult }
import uk.gov.hmrc.gform.exceptions.UnexpectedState
import uk.gov.hmrc.gform.sharedmodel._
import uk.gov.hmrc.gform.sharedmodel.formtemplate._
import uk.gov.hmrc.gform.services.RepeatingComponentService
import uk.gov.hmrc.gform.sharedmodel.form.FormField

import scala.collection.immutable.List

object FormTemplateValidator {

  def validateUniqueFields(sectionsList: List[Section]): ValidationResult = {
    val fieldIds: List[FieldId] = sectionsList.flatMap(_.fields.map(_.id))
    val duplicates: List[FieldId] = fieldIds.groupBy(identity).collect { case (fId, List(_, _, _*)) => fId }.toList

    duplicates.isEmpty match {
      case true => Valid
      case false => Invalid(s"Some FieldIds are defined more than once: ${duplicates.map(_.value)}")
    }
  }

  def validateChoiceHelpText(sectionsList: List[Section]): ValidationResult = {
    val choiceFieldIdMap: Map[FieldId, Boolean] = sectionsList.flatMap(_.fields).map(fv => (fv.id, fv.`type`))
      .collect {
        case (fId, Choice(_, options, _, _, helpTextList @ Some(x :: xs))) =>
          (fId, options.toList.size.equals(helpTextList.getOrElse(List.empty).size))
      }
      .toMap

    val choiceFieldIdResult = choiceFieldIdMap.filter(value => value._2.equals(false))

    choiceFieldIdResult.isEmpty match {
      case true => Valid
      case false => Invalid(s"Choice components doesn't have equal number of choices and help texts ${choiceFieldIdResult.keys.toList}")
    }
  }

  private def getMandatoryAndOptionalFields(section: Section): (Set[FieldId], Set[FieldId]) = {
    SectionHelper.atomicFields(section, Map.empty).foldLeft((Set.empty[FieldId], Set.empty[FieldId])) {
      case ((mandatoryAcc, optionalAcc), field) =>
        (field.`type`, field.mandatory) match {
          case (Address(_), _) => (mandatoryAcc ++ Address.mandatoryFields(field.id), optionalAcc ++ Address.optionalFields(field.id))
          case (Date(_, _, _), _) => (mandatoryAcc ++ Date.fields(field.id), optionalAcc)
          case (Text(_, _, _), true) => (mandatoryAcc + field.id, optionalAcc)
          case (Text(_, _, _), false) => (mandatoryAcc, optionalAcc + field.id)
          case (_, true) => (mandatoryAcc + field.id, optionalAcc)
          case (_, false) => (mandatoryAcc, optionalAcc + field.id)
        }
    }
  }

  /**
   * Tries to find section in form template corresponding to submitted data.
   *
   * Section is determined by these rules:
   * - all mandatory fields from the section must be present in submission
   * - optional fields from section don't need to be present in submission
   * - presence of any other field than those from form template is resulting in failed location of a section
   */
  def getMatchingSection(formFields: Seq[FormField], sections: Seq[Section]): Opt[Section] = {
    val formFieldIds: Set[FieldId] = formFields.map(_.id).toSet
    val sectionOpt: Option[Section] = sections.find { section =>

      val (mandatorySectionIds, optionalSectionIds) = getMandatoryAndOptionalFields(section)

      val missingMandatoryFields = mandatorySectionIds diff formFieldIds

      val optionalFieldsFromSubmission = RepeatingComponentService.discardRepeatingFields(
        formFieldIds diff mandatorySectionIds,
        mandatorySectionIds,
        optionalSectionIds
      )

      val fieldWhichAreNotFromFormTemplate = optionalFieldsFromSubmission diff optionalSectionIds

      missingMandatoryFields.isEmpty && fieldWhichAreNotFromFormTemplate.isEmpty
    }

    sectionOpt match {
      case Some(section) => Right(section)
      case None =>
        val sectionsForPrint = sections.map(_.fields.map(_.id))

        Left(UnexpectedState(s"""|Cannot find a section corresponding to the formFields
                                 |FormFields: $formFieldIds
                                 |Sections: $sectionsForPrint""".stripMargin))
    }
  }

  def validate(exprs: List[ComponentType], formTemplate: FormTemplate): ValidationResult = {
    val results = exprs.map(validate(_, formTemplate))
    Monoid[ValidationResult].combineAll(results)
  }

  def validate(componentType: ComponentType, formTemplate: FormTemplate): ValidationResult = componentType match {
    case Text(_, expr, _) => expr.validate(formTemplate)
    case Date(_, _, _) => Valid
    case Address(_) => Valid
    case Choice(_, _, _, _, _) => Valid
    case Group(fvs, _, _, _, _, _) => FormTemplateValidator.validate(fvs.map(_.`type`), formTemplate)
    case FileUpload() => Valid
    case InformationMessage(_, _) => Valid
  }

}