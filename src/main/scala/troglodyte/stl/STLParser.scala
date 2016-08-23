package troglodyte.stl

import org.apache.poi.ss.usermodel.Cell
import org.parboiled2._
import troglodyte.stl.Translator.Transformer

import scala.util.{Failure, Success}

object STLParser {
  type Task = Map[String, Any]
  case class STLPair(key: String, value: Any)
  case class STLAddressRange(addressRange: String)
  case class STLFunction(name: String, arguments: Seq[Any])
  case class STLExpression(functions: Seq[STLFunction])
  case class STLColumnDefinition(keyFn: STLExpression, valueFn: STLExpression)
  case class STLParserException(message: String) extends Exception(message)

  val stlFunctions = Map(
    "fixedString" -> Operators.fixedString,
    "columnHeading" -> Operators.columnHeading,
    "column" -> Operators.columnHeading,
    "valueOr" -> Operators.valueOr,
    "value" -> Operators.value
  )

  def parseToTask(stl: String): Task = {
    val parser = new RealSTLParser(stl.trim)
    val res = parser.InputLine.run()
    res match {
      case Success(rel) => outputToTask(rel)
      case Failure(pe:ParseError) => throw STLParserException(s"Could not parse STL: \n${parser.formatError(pe)}")
      case _ => throw new Exception("oops!")
    }
  }

  def makeKeyFn(stlExpression: STLExpression): (Cell => String) = {
    stlExpression.functions.map(stlFunction => {
      (stlFunction.name match {
        case "fixedString" => Operators.fixedString(stlFunction.arguments(0).asInstanceOf[String])
        case "columnHeading" => Operators.columnHeading(stlFunction.arguments(0).asInstanceOf[String], stlFunction.arguments(1).asInstanceOf[String])
        case "column" => Operators.column(stlFunction.arguments(0).asInstanceOf[String].toInt)
        case "valueOr" => Operators.valueOr(stlFunction.arguments(0).asInstanceOf[String])
        case "value" => Operators.value
      }).asInstanceOf[Any => Any]
    }).reduce((a, b) => a.compose(b)).asInstanceOf[Cell => String]
  }

  def makeValueFn(stlExpression: STLExpression): (Cell => Any) = {
    stlExpression.functions.map(stlFunction => {
      (stlFunction.name match {
        case "fixedString" => Operators.fixedString(stlFunction.arguments(0).asInstanceOf[String])
        case "columnHeading" => Operators.columnHeading(stlFunction.arguments(0).asInstanceOf[String], stlFunction.arguments(1).asInstanceOf[String])
        case "column" => Operators.column(stlFunction.arguments(0).asInstanceOf[String].toInt)
        case "valueOr" => Operators.valueOr(stlFunction.arguments(0).asInstanceOf[String])
        case "value" => Operators.value
      }).asInstanceOf[Any => Any]
    }).reduce((a, b) => b.compose(a)).asInstanceOf[Cell => Any]
  }

  def outputToTask(pairs: Seq[Any]): Task = {
    val toTaskKey = Map("NAME" -> "name", "SHEET" -> "sheet", "KEY_CELLS" -> "keyCells")
    val map = pairs
      .filter(_.isInstanceOf[STLPair])
      .map(_.asInstanceOf[STLPair])
      .map(pair => (toTaskKey(pair.key), pair.value))
      .toMap
    map
      .updated("keyCells", map("keyCells").asInstanceOf[STLAddressRange].addressRange)
      .updated("attributes",
        pairs
          .filter(_.isInstanceOf[STLColumnDefinition])
          .map(_.asInstanceOf[STLColumnDefinition])
          .map(column => Transformer(makeKeyFn(column.keyFn), makeValueFn(column.valueFn)))
          .toList)
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
