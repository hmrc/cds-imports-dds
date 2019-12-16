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

package integration.uk.gov.hmrc.cdsimportsdds.repositories

import java.time.{LocalDate, ZoneOffset}

import com.codahale.metrics.SharedMetricRegistries
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import play.api.inject.guice.GuiceApplicationBuilder
import reactivemongo.api.ReadConcern
import uk.gov.hmrc.cdsimportsdds.repositories.DeclarationRepository
import uk.gov.hmrc.cdsimportsdds.utils.ImportsDeclarationBuilder

import scala.concurrent.ExecutionContext.Implicits.global

class DeclarationRepositoryTest extends WordSpec
  with Matchers
  with ScalaFutures
  with BeforeAndAfterEach
  with ImportsDeclarationBuilder
  with IntegrationPatience {

  private val injector = {
    SharedMetricRegistries.clear()
      GuiceApplicationBuilder().injector()
  }

  private val repository = injector.instanceOf[DeclarationRepository]

  override def beforeEach(): Unit = {
    super.beforeEach()
    repository.removeAll().futureValue
  }

  private def collectionSize: Int =
    repository.collection
      .count(selector = None, limit = Some(0), skip = 0, hint = None, readConcern = ReadConcern.Local)
      .futureValue
      .toInt

  "Create" should {
    "persist the declaration" in {
      val declaration = anImportsDeclaration
      repository.create(declaration).futureValue shouldBe declaration

      collectionSize shouldBe 1
    }
  }

}
