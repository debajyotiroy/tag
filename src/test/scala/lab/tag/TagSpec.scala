package lab.tag

import _root_.argonaut._
import _root_.argonaut.Argonaut._
import org.scalatest.prop.Checkers
import org.scalatest.{FlatSpec, Matchers}

class TagSpec extends FlatSpec with Matchers with Checkers {

  "The Tag codec" should "correctly decode JSON to Tag" in {
    check{ t: Tag =>
      val json = t.asJson.toString
      Parse.decodeOption[Tag](json) === Some(t)
    }
  }

  it should "correctly encode a Tag to JSON" in {
    check{ t: Tag =>
      Parse.decodeOption[Tag](t.asJson.nospaces) === Some(t)
    }
  }
}
