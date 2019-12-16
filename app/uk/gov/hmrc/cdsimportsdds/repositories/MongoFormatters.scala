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

package uk.gov.hmrc.cdsimportsdds.repositories

import java.time.Instant

import play.api.libs.json._
import uk.gov.hmrc.cdsimportsdds.models.ImportsDeclaration

object MongoFormatters {

  implicit val formatInstant: OWrites[Instant] = new OWrites[Instant] {
    override def writes(datetime: Instant): JsObject =
      Json.obj("$date" -> datetime.toEpochMilli)
  }

  implicit val format: OFormat[ImportsDeclaration] = Json.format[ImportsDeclaration]
}
