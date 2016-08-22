package troglodyte.stl

import org.parboiled2._

import scala.util.{Failure, Success}

object STLParser {
  type Task = Map[String, Any]
  case class STLPair(key: String, value: Any)
  case class STLAddressRange(addressRange: String)
  case class STLFunction(name: String, arguments: Seq[Any])
  case class STLExpression(functions: Seq[STLFunction])
  case class STLColumnDefinition(keyFn: STLExpression, valueFn: STLExpression)
  case class STLParserException(message: String) extends Exception(message)

  def parseToTask(stl: String): Task = {
    val parser = new RealSTLParser(stl.trim)
    val res = parser.InputLine.run()
    res match {
      case Success(rel) => outputToTask(rel)
      case Failure(pe:ParseError) => throw STLParserException(s"Could not parse STL: \n${parser.formatError(pe)}")
    }
  }

  def outputToTask(pairs: Seq[Any]): Task = {
    val toTaskKey = Map("NAME" -> "name", "SHEET" -> "sheet", "KEY_CELLS" -> "keyCells")
    pairs
      .filter(_.isInstanceOf[STLPair])
      .map(_.asInstanceOf[STLPair])
      .map(pair => (toTaskKey(pair.key), pair.value))
      .toMap
      .updated("attributes",
        pairs
          .filter(_.isInstanceOf[STLColumnDefinition])
          .map(_.asInstanceOf[STLColumnDefinition])
          .map(column => (column.keyFn, column.valueFn)))

  }

  class RealSTLParser(val input: ParserInput) extends Parser {
    def InputLine = rule { oneOrMore(Statement).separatedBy(oneOrMore(NewLine)) ~ EOI }

    def Statement = rule { KeyValueStatement | ColumnStatement }

    def KeyValueStatement = rule { Spaces ~ Action ~ Spaces ~ Value ~ Spaces ~> STLPair }

    def ColumnStatement = rule { "COLUMN" ~ Spaces ~ Expression ~ optional(Spaces ~ NewLine ~ Spaces) ~ "=>" ~ Spaces ~ Expression ~ Spaces ~> STLColumnDefinition }

    def Expression = rule { oneOrMore(Function).separatedBy(Spaces ~ "|" ~ Spaces) ~> STLExpression }

    def Function = rule { FunctionName ~ (ArgumentList | push(Vector())) ~> STLFunction }

    def FunctionName = rule { capture(oneOrMore(CharPredicate.Alpha)) }

    def ArgumentList = rule { "(" ~ zeroOrMore(Argument).separatedBy(Spaces ~ "," ~ Spaces) ~ ")" }

    def Argument = rule { StringLiteral | NumberLiteral }

    def Action = rule { capture("NAME" | "SHEET" | "KEY_CELLS") }

    def Value = rule { StringLiteral | AddressRangeLiteral }

    def AddressRangeLiteral = rule { "[" ~ capture(AddressRange) ~> STLAddressRange ~ "]"}

    def AddressRange = rule { CellRef ~ ":" ~ CellRef }

    def CellRef = rule { oneOrMore(CharPredicate.UpperAlpha) ~ oneOrMore(CharPredicate.Digit) }

    def StringLiteral = rule { '"' ~ capture(zeroOrMore(noneOf("\""))) ~ '"' }

    def NumberLiteral = rule { capture(oneOrMore(CharPredicate.Digit)) }

    def Spaces = rule { zeroOrMore(CharPredicate(" \t")) }

    def NewLine = rule { optional('\r') ~ '\n' }
  }
}
