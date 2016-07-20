package lab

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

package object tag {
  implicit val tagArbitrary: Arbitrary[Tag] = Arbitrary(
    for {
      id <- arbitrary[Option[Long]]
      name <- arbitrary[String] suchThat (s => s != null && s.nonEmpty)
    } yield Tag(id, name)
  )
}
