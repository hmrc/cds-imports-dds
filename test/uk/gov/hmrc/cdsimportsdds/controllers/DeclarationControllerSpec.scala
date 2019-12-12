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

package uk.gov.hmrc.cdsimportsdds.controllers

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers._
import org.mockito.BDDMockito._
import org.mockito.Mockito.{reset, verify, verifyZeroInteractions}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterEach, MustMatchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.json.Json.toJson
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers.{CREATED, contentAsJson, route, status, _}
import uk.gov.hmrc.cdsimportsdds.models.ImportsDeclaration
import uk.gov.hmrc.cdsimportsdds.services.DeclarationService
import uk.gov.hmrc.cdsimportsdds.util.RESTFormatters.formatImportsDeclaration
import uk.gov.hmrc.cdsimportsdds.utils.ImportsDeclarationBuilder
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.{FiniteDuration, _}

class DeclarationControllerSpec extends WordSpec
  with GuiceOneAppPerSuite
  with BeforeAndAfterEach
  with ScalaFutures
  with MustMatchers
  with MockitoSugar
  with ImportsDeclarationBuilder {

  override lazy val app: Application = GuiceApplicationBuilder()
    .overrides(bind[DeclarationService].to(declarationService))
    .build()

  private val declarationService: DeclarationService = mock[DeclarationService]

  implicit val defaultTimeout: FiniteDuration = 5 seconds

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(declarationService)
  }

  "POST /declarations" should {
    val post = FakeRequest("POST", "/declarations")
    val importDeclarationRequest = ImportsDeclarationRequest(lrn="lrn")
    val eori = "eori"

    "return 201" when {
      "request is valid" in {
        val declaration = anImportsDeclaration.copy(eori = eori)
        given(declarationService.create(any[ImportsDeclaration])(any[HeaderCarrier], any[ExecutionContext]))
          .willReturn(Future.successful(declaration))

        val result: Future[Result] = route(app, post.withHeaders(("X-EORI-Number", eori))
                                                    .withJsonBody(toJson(importDeclarationRequest))).get

        status(result) must be(CREATED)
        contentAsJson(result) mustBe toJson(declaration)
        theDeclarationCreated.eori mustBe eori
      }
    }

    "return 400" when {
      "invalid json" in {
        val result: Future[Result] = route(app, post.withHeaders(("X-EORI-Number", eori))
                                                    .withJsonBody(toJson("lrn1234"))).get

        status(result) must be(BAD_REQUEST)
        contentAsJson(result) mustBe Json.obj("message" -> "Bad Request", "errors" -> Json.arr(": error.expected.jsobject"))
        verifyZeroInteractions(declarationService)
      }

      "lrn is missing" in {
        val result: Future[Result] = route(app, post.withHeaders(("X-EORI-Number", eori))
                                                    .withJsonBody(Json.obj("foo" -> "123"))).get

        status(result) must be(BAD_REQUEST)
        contentAsJson(result) mustBe Json.obj("message" -> "Bad Request", "errors" -> Json.arr("/lrn: error.path.missing"))
        verifyZeroInteractions(declarationService)
      }

      "eori is missing" in {
        val result: Future[Result] = route(app, post.withJsonBody(toJson(importDeclarationRequest))).get

        status(result) must be(BAD_REQUEST)
        contentAsJson(result) mustBe Json.obj("message" -> "X-EORI-Number header missing")
        verifyZeroInteractions(declarationService)
      }
    }

  }

  def theDeclarationCreated: ImportsDeclaration = {
    val captor: ArgumentCaptor[ImportsDeclaration] = ArgumentCaptor.forClass(classOf[ImportsDeclaration])
    verify(declarationService).create(captor.capture())(any[HeaderCarrier], any[ExecutionContext])
    captor.getValue
  }

}
