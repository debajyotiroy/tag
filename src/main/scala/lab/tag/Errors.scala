package lab.tag

sealed abstract class TagError(msg: String) extends Exception(msg) {
  def message: String
}

case class InvalidInput(message: String) extends TagError(message)

case class MissingIdentifier(message: String) extends TagError(message)
