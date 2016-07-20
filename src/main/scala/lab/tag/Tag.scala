package lab.tag

import _root_.argonaut._
import argonaut.Argonaut._

case class Tag(
                id: Option[Long],
                name: String
              )

object Tag {
  implicit val tagCodec: CodecJson[Tag] = //instance of a type class
    casecodec2(Tag.apply, Tag.unapply)("id", "name")
}
