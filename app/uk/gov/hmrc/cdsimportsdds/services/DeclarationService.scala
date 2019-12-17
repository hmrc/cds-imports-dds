/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.cdsimportsdds.services

import javax.inject.Inject
import uk.gov.hmrc.cdsimportsdds.domain.Eori
import uk.gov.hmrc.cdsimportsdds.models.ImportsDeclaration
import uk.gov.hmrc.cdsimportsdds.repositories.DeclarationRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class DeclarationService @Inject()(declarationRepository: DeclarationRepository) {

  def create(declaration: ImportsDeclaration): Future[ImportsDeclaration] =
    declarationRepository.create(declaration)

  def findByEori(eori: Eori): Future[Seq[ImportsDeclaration]] =
    declarationRepository.findByEori(eori)

}
