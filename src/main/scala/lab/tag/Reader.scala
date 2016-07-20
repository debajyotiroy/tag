package lab.tag

import io.finch._

object Reader {
  implicit val kvsReader: RequestReader[Seq[String]] = param("kvs").map { tags =>
    tags.split(",").map(_.trim)
  }
  implicit val idReader: RequestReader[String] = param("id")
}