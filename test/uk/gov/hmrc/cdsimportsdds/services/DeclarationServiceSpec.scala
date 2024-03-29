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

import org.mockito.ArgumentMatchers.{eq => meq}
import org.mockito.BDDMockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.cdsimportsdds.models.ImportsDeclaration
import uk.gov.hmrc.cdsimportsdds.repositories.DeclarationRepository
import uk.gov.hmrc.cdsimportsdds.utils.ImportsDeclarationBuilder

import scala.concurrent.Future

class DeclarationServiceSpec extends WordSpec
  with MockitoSugar
  with ScalaFutures
  with MustMatchers
  with ImportsDeclarationBuilder {

  private val declarationRepository = mock[DeclarationRepository]
  private val service = new DeclarationService(declarationRepository)

  "Create" should {
    "delegate to the repository" in {
      val declaration = anImportsDeclaration
      val persistedDeclaration = mock[ImportsDeclaration]
      given(declarationRepository.create(meq(declaration))).willReturn(Future.successful(persistedDeclaration))

      service.create(declaration).futureValue mustBe persistedDeclaration
    }
  }

  "findByEori" should {
    "delegate to the repository" in {
      val eori = "GB1234"
      val persistedDeclaration = mock[ImportsDeclaration]
      given(declarationRepository.findByEori(meq(eori))).willReturn(Future.successful(Seq(persistedDeclaration)))

      service.findByEori(eori).futureValue mustBe Seq(persistedDeclaration)
    }
  }

}
