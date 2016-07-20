package lab.tag

import _root_.argonaut._
import argonaut.Argonaut._
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import io.finch._
import io.finch.argonaut._
import io.finch.items._

object Endpoint {

  implicit val encodeException: EncodeJson[Exception] = EncodeJson {
    case Error.NotPresent(ParamItem(p)) => Json.obj(
      "error" -> jString("param_not_present"), "param" -> jString(p)
    )
    case Error.NotPresent(BodyItem) => Json.obj(
      "error" -> jString("body_not_present")
    )
    case Error.NotParsed(ParamItem(p), _, _) => Json.obj(
      "error" -> jString("param_not_parsed"), "param" -> jString(p)
    )
    case Error.NotParsed(BodyItem, _, _) => Json.obj(
      "error" -> jString("body_not_parsed")
    )
    case Error.NotValid(ParamItem(p), rule) => Json.obj(
      "error" -> jString("param_not_valid"), "param" -> jString(p), "rule" -> jString(rule)
    )
    // Domain errors
    case error: TagError => Json.obj(
      "error" -> jString(error.message)
    )
  }

  def makeService(store: Store): Service[Request, Response] = (
    readTag(store) :+:
      createTag(store) :+:
      updateTag(store) :+:
      deleteTag(store) :+:
      tags(store)
    ).handle({ case e: TagError => NotFound(e)}).toService

  def readTag(store: Store): Endpoint[Tag] =
    get("tag" / long) { id: Long => Ok(store.readTag(id)) }

  def createTag(store: Store): Endpoint[Long] =
    post("tag" ? body.as[Tag]) { t: Tag =>
      Ok(store.addTag(t))
    }

  def updateTag(store: Store): Endpoint[Tag] =
    put("tag" ? body.as[Tag]) { t: Tag =>
      t.id match {
        case Some(num) => Ok(store.updateTag(t))
        case None => throw MissingIdentifier("The updated tag must have a valid id.")
      }
    }

  def deleteTag(store: Store): Endpoint[Unit] =
    delete("tag" / long) { tId: Long =>
      Ok(store.deleteTag(tId))
    }

  def tags(store: Store): Endpoint[Seq[Tag]] =
    get("tag" / string) { command: String =>
      command match {
        case "all" => Ok(store.allTags())
        case _ => throw InvalidInput("Invalid command")
      }
    }
}
