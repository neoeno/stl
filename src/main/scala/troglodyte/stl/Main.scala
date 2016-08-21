package troglodyte.stl

import java.io.File

import org.apache.poi.ss.usermodel._
import troglodyte.stl.Operators._

object Main extends App {
  val task = Map(
    "sheet" -> "Violence",
    "keyCells" -> "A4:A101",
    "attributeNameFns" -> List(
      columnHeading("A3", "Date"),
      fixedString("VAP Offences")
    ),
    "attributeValueFns" -> List(
      value,
      column(1).andThen(value)
    )
  )

  val workbook = WorkbookFactory.create(new File("examples/1/mps-figures.xls"))

  println(
    Translator.extractRecordsByTask(task)(workbook).toList
  )
}
