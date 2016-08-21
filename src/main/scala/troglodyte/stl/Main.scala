package troglodyte.stl

import java.io.File

import org.apache.poi.ss.usermodel.Row.MissingCellPolicy
import org.apache.poi.ss.usermodel._

object Main extends App {
  val task = Map(
    "sheet" -> "Violence",
    "keyCells" -> "A4:A101",
    "attributeNameFns" -> List(
      (cell: Cell) => "Date",
      (cell: Cell) => "VAP Offences"
    ),
    "attributeValueFns" -> List(
      (cell: Cell) => Spreadsheet.getCellValue(cell),
      (cell: Cell) => Spreadsheet.getCellValue(cell.getRow.getCell(1, MissingCellPolicy.RETURN_BLANK_AS_NULL))
    )
  )

  val workbook = WorkbookFactory.create(new File("examples/1/mps-figures.xls"))

  println(
    Translator.extractRecordsByTask(task)(workbook).toList
  )
}
