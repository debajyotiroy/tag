package lab.tag

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, RequestBuilder, Response}
import com.twitter.io.Buf
import io.finch.test.ServiceSuite
import org.scalatest.Matchers
import org.scalatest.fixture.FlatSpec

trait TagServiceSuite {this: FlatSpec with ServiceSuite with Matchers =>
  def createService(): Service[Request, Response] = {
    val store = new Store
    store.addTag(Tag(None, "One"))
    store.addTag(Tag(None, "Two"))
    store.addTag(Tag(None, "Three"))
    store.addTag(Tag(None, "Four"))

    Endpoint.makeService(store)
  }

  "The TagService" should "return valid tags" in { f =>
    val request = Request("/tag/1")
    val result: Response = f(request)
    result.statusCode shouldBe 200
  }

  it should "fail to return invalid tags" in { f =>
    val request = Request("/tag/100")
    val result: Response = f(request)
    result.statusCode shouldBe 404
  }

  it should "add valid tags" in { f =>
    val request: Request = RequestBuilder()
      .url("http://localhost:8080/tag").buildPost(
      Buf.Utf8(s"""
                  |  {
                  |    "id": 5
                  |    "name": "five"
                  |  }
           """.stripMargin)
    )
    val result: Response = f(request)
    result.statusCode shouldBe 200
  }

  it should "fail appropriately when adding invalid tags" in { f =>
    val request: Request = RequestBuilder()
      .url("http://localhost:8080/tag").buildPost(
      Buf.Utf8(s"""
                  |  {
                  |  "foo": "bar"
                  |  }
         """.stripMargin)
    )
    val result: Response = f(request)

    result.statusCode shouldBe 404
  }

  it should "update valid tags" in { f =>
    val request: Request = RequestBuilder()
      .url("http://localhost:8080/tag").buildPut(
      Buf.Utf8(s"""
                  |  {
                  |    "id": 1,
                  |    "name": "updatedOne"
                  |  }
           """.stripMargin))
    val result: Response = f(request)

    result.statusCode shouldBe 200
  }

  it should "fail attempts to update tags without specifying an ID to modify" in { f =>
    val request: Request = RequestBuilder()
      .url("http://localhost:8080/tag").buildPut(
      Buf.Utf8(s"""
                  |  {
                  |    "name": "missing id"
                  |  }
           """.stripMargin))
    val result: Response = f(request)

    result.statusCode shouldBe 404
  }
}

class TagServiceSpec extends FlatSpec with ServiceSuite with TagServiceSuite with Matchers
