package lab.tag

import java.util.concurrent.atomic.AtomicLong
import scala.collection.concurrent.TrieMap
import com.twitter.util.Future

class Store {
  private[this] val tags = new TrieMap[Long, Tag]()
  private[this] val idGen = new AtomicLong(0L)

  def readTag(id: Long): Future[Tag] = Future{tags(id)}

  def addTag(t: Tag): Future[Long] = Future{
    val newId = idGen.incrementAndGet()
    tags += (newId -> t.copy(id = Some(newId)))
    newId
  }

  def updateTag(t: Tag): Future[Tag] = Future{
    tags += (t.id.get -> t)
    t
  }

  def deleteTag(id: Long): Future[Unit] =
    if (tags.contains(id)) {
      tags -= id
      Future.Unit
    } else Future.exception(
      MissingIdentifier(s"Pet with id $id does not exist and cannot be deleted")
    )

  def allTags(): Future[Seq[Tag]] = Future(tags.values.toSeq.sortBy(_.id))
}
