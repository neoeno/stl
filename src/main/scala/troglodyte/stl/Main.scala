package troglodyte.stl

import java.io.File

import org.apache.poi.ss.usermodel.Row.MissingCellPolicy
import org.apache.poi.ss.usermodel._

object Main extends App {
  type Task = Map[String, Any]

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

  def extractRecords(task: Task)(workbook: Workbook): Iterator[Map[String, Any]] = {
    val sheetName = task("sheet").asInstanceOf[String]
    val keyCellsRange = task("keyCells").asInstanceOf[String]
    val attributeNameFns = task("attributeNameFns").asInstanceOf[List[Cell => String]]
    val attributeValueFns = task("attributeValueFns").asInstanceOf[List[Cell => Any]]

    Spreadsheet.cellsInRange(
      Spreadsheet.getSheet(workbook)(sheetName)
    )(keyCellsRange).map(
      Spreadsheet.constructMapWithFns(attributeNameFns, attributeValueFns)
    )
  }

  val workbook = WorkbookFactory.create(new File("examples/1/mps-figures.xls"))

  println(
    extractRecords(task)(workbook).toList
  )
}
