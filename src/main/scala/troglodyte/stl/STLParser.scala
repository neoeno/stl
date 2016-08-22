package troglodyte.stl

import org.parboiled2._

import scala.util.{Failure, Success}

object STLParser {
  type Task = Map[String, Any]
  case class KVPair(key: String, value: String)
  case class STLParserException(message: String) extends Exception(message)

  def parseToTask(stl: String): Task = {
    val parser = new RealSTLParser(stl.trim)
    val res = parser.InputLine.run()
    res match {
      case Success(rel) => outputToTask(rel)
      case Failure(pe:ParseError) => throw STLParserException(s"Could not parse STL: \n${parser.formatError(pe)}")
    }
  }

  def outputToTask(pairs: Seq[KVPair]): Task = {
    val toTaskKey = Map("NAME" -> "name", "SHEET" -> "sheet")
    pairs.map(pair => (toTaskKey(pair.key), pair.value)).toMap
  }

  class RealSTLParser(val input: ParserInput) extends Parser {
    def InputLine = rule { oneOrMore(Statement).separatedBy(NewLine) ~ EOI }

    def Statement = rule { Spaces ~ Action ~ Spaces ~ Value ~ Spaces ~> KVPair }

    def Action = rule { capture(oneOrMore(CharPredicate.UpperAlpha)) }

    def Value = rule { StringLiteral }

    def StringLiteral = rule { '"' ~ capture(zeroOrMore(noneOf("\""))) ~ '"' }

    def Spaces = rule { zeroOrMore(CharPredicate(" \t")) }

    def NewLine = rule { optional('\r') ~ '\n' }
  }
}
