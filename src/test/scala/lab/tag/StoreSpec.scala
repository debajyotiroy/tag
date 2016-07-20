package lab.tag

import com.twitter.util.{Future, Await}
import org.scalatest.prop.Checkers
import org.scalatest.{FlatSpec, Matchers}

class StoreSpec extends FlatSpec with Matchers with Checkers {

  val one = Tag(None, "One")
  val two = Tag(None, "Two")
  val three = Tag(None, "Three")
  val four = Tag(None, "Four")

  trait DbContext {
    val store = new Store()
    Await.ready(store.addTag(one))
    Await.ready(store.addTag(two))
    Await.ready(store.addTag(three))
    Await.ready(store.addTag(four))
  }

  "The Store" should "allow tag lookup by id" in new DbContext {
    assert(Await.result(store.readTag(1L)) === one.copy(id = Some(1L)))
  }

  it should "fail appropriately when asked to get tag ids that don't exist" in new DbContext {
    Await.result(store.readTag(100L).liftToTry).isThrow shouldBe true
  }

  it should "allow adding tags" in new DbContext {
    check { (t: Tag) =>
      val tInput = t.copy(id = None)

      val result = for {
        tId <- store.addTag(tInput)
        newTag <- store.readTag(tId)
      } yield newTag === t.copy(id = Some(tId))

      Await.result(result)
    }
  }

  it should "fail appropriately when asked to add invalid tags" in new DbContext{
    val invalid:Tag = null
    Await.result(store.addTag(invalid).liftToTry).isThrow shouldBe true
  }

  it should "allow for the updating of existing tags via new Tag object" in new DbContext {
    check{ (t:Tag) =>
      val newT = t.copy(id = Some(0))
      store.updateTag(newT)
      val result = for{
        optPet <- store.readTag(0)
      } yield optPet === newT

      Await.result(result)
    }
  }

  it should "fail to update tags when replacements are passed with no ID" in new DbContext {
    check{ (t: Tag) =>
      val noT = t.copy(id = None)
      val f = store.updateTag(noT)
      Await.result(f.liftToTry).isThrow
    }
  }

  it should "allow the deletion of existing tags" in new DbContext{
    val delT = Tag(None, "toBeDeleted")
    val genId: Long = Await.result(store.addTag(delT))

    val success: Future[Unit] = store.deleteTag(genId) //There WILL be an ID
    Await.ready(success)
  }

  it should "fail appropriately if user tries to delete a nonexistant tag" in new DbContext{
    val noSuchTag = Tag(Some(10), "NST")
    assert(Await.result(store.deleteTag(noSuchTag.id.getOrElse(-1)).liftToTry).isThrow)
  }

  it should "allow to get all Tags" in new DbContext{
    var ts: Seq[Tag] = Await.result(store.allTags())
    for(t <- ts){
      assert(t.id.getOrElse(-1L) !== -1L)
    }
  }
}
