package troglodyte.stl

import org.apache.poi.ss.usermodel.Cell
import org.parboiled2._
import troglodyte.stl.Translator.Transformer

import scala.util.{Failure, Success}

object STLParser {
  type Task = Map[String, Any]
  case class STLPair(key: String, value: Any)
  case class STLAddressRange(addressRange: String)
  case class STLColumnDefinition(keyFn: Function[Any, Any], valueFn: Function[Any, Any])
  case class STLParserException(message: String) extends Exception(message)

  def parseToTask(stl: String): Task = {
    val parser = new RealSTLParser(stl.trim)
    val res = parser.InputLine.run()
    res match {
      case Success(rel) => outputToTask(rel)
      case Failure(pe:ParseError) => throw STLParserException(s"Could not parse STL: \n${parser.formatError(pe)}")
      case e: Throwable => throw new Exception("oops!", e)
    }
  }

  def outputToTask(pairs: Seq[Any]): Task = {
    val map = pairs
      .filter(_.isInstanceOf[STLPair])
      .map(_.asInstanceOf[STLPair])
      .map(pair => (pair.key, pair.value))
      .toMap
    map
      .updated("attributes",
        pairs
          .filter(_.isInstanceOf[STLColumnDefinition])
          .map(_.asInstanceOf[STLColumnDefinition])
          .map(column => Transformer(column.keyFn.asInstanceOf[Cell => String], column.valueFn.asInstanceOf[Cell => Any]))
          .toList)
  }

  object STLOperators {
    val fixedString = (args: Seq[Any]) => {
      Operators.fixedString(args(0).asInstanceOf[String]).asInstanceOf[Any => Any]
    }

    val columnHeading = (args: Seq[Any]) => {
      Operators.columnHeading(args(0).asInstanceOf[String], args(1).asInstanceOf[String]).asInstanceOf[Any => Any]
    }

    val column = (args: Seq[Any]) => {
      Operators.column(args(0).asInstanceOf[Int]).asInstanceOf[Any => Any]
    }

    val valueOr = (args: Seq[Any]) => {
      Operators.valueOr(args(0).asInstanceOf[String]).asInstanceOf[Any => Any]
    }

    val value = (args: Seq[Any]) => {
      Operators.value.asInstanceOf[Any => Any]
    }
  }

  class RealSTLParser(val input: ParserInput) extends Parser {
    val actionTypes = Map("NAME" -> "name", "SHEET" -> "sheet", "KEY_CELLS" -> "keyCells")
    val stlFunctions = Map(
      "fixedString" -> STLOperators.fixedString,
      "columnHeading" -> STLOperators.columnHeading,
      "column" -> STLOperators.column,
      "valueOr" -> STLOperators.valueOr,
      "value" -> STLOperators.value
    )

    def partialApplyFn = (fn: Seq[Any] => Any => Any, args: Seq[Any]) => fn(args)
    def chainFns = (fns: Seq[Any => Any]) => Function.chain(fns)
    def updateMap = (key: String, value: Any, map: Map[String, Any]) => map.updated(key, value)

    def InputLine = rule { oneOrMore(Statement).separatedBy(oneOrMore(NewLine)) ~ EOI }

    def Statement = rule { KeyValueStatement | ColumnStatement }

    def KeyValueStatement = rule { Spaces ~ Action ~ Spaces ~ Value ~ Spaces ~> STLPair }

    def ColumnStatement = rule { "COLUMN" ~ Spaces ~ Expression ~ optional(Spaces ~ NewLine ~ Spaces) ~ "=>" ~ Spaces ~ Expression ~ Spaces ~> STLColumnDefinition }

    def Expression = rule { oneOrMore(ExpressionFunction).separatedBy(Spaces ~ "|" ~ Spaces) ~> chainFns }

    def ExpressionFunction = rule { FunctionName ~ (ArgumentList | push(Vector())) ~> partialApplyFn }

    def FunctionName = rule { valueMap(stlFunctions) }

    def ArgumentList = rule { "(" ~ zeroOrMore(Argument).separatedBy(Spaces ~ "," ~ Spaces) ~ ")" }

    def Argument = rule { StringLiteral | NumberLiteral }

    def Action = rule { valueMap(actionTypes) }

    def Value = rule { StringLiteral | AddressRangeLiteral }

    def AddressRangeLiteral = rule { "[" ~ capture(AddressRange) ~ "]"}

    def AddressRange = rule { CellRef ~ ":" ~ CellRef }

    def CellRef = rule { oneOrMore(CharPredicate.UpperAlpha) ~ oneOrMore(CharPredicate.Digit) }

    def StringLiteral = rule { '"' ~ capture(zeroOrMore(noneOf("\""))) ~ '"' }

    def NumberLiteral = rule { capture(oneOrMore(CharPredicate.Digit)) ~> (_.toInt) }

    def Spaces = rule { zeroOrMore(CharPredicate(" \t")) }

    def NewLine = rule { optional('\r') ~ '\n' }
  }
}
