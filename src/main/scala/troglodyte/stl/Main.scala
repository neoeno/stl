package troglodyte.stl

import java.io.File

import org.apache.poi.ss.usermodel._
import org.apache.poi.ss.util.{CellRangeAddress, RegionUtil}

import scala.collection.JavaConverters._

object Main extends App {
  type CellCoordinate = (Int, Int)
  case class SheetRange(start: CellCoordinate, end: Option[CellCoordinate])
  case class SheetArea(sheet: Sheet, range: SheetRange)
  case class ColumnNumber(n: Int)
  case class RowNumber(n: Int)
  type Attribute = (String, CellCoordinate => Any)
  type Record = Map[String, Any]

  type Task = Map[String, Any]
  val task = Map(
    "sheet" -> "Violence",
    "area" -> SheetRange(new CellCoordinate(0, 2), None),
    "attributeLabels" -> RowNumber(2),
    "recordKeys" -> ColumnNumber(0),
    "attributeTypes" -> Map(
      "Month-Year" -> "Date",
      "Violence with injury (VWI) SDR" -> "Percentage",
      "Knife SDR" -> "Percentage",
      "Gun Crime SDR" -> "Percentage",
      "Dog Attacks SDR" -> "Percentage"
    )
  )

  val workbook = WorkbookFactory.create(new File("examples/1/mps-figures.xls"))

  def extractSheet(task: Task)(workbook: Workbook): Sheet = {
    val sheetName = task("sheet").asInstanceOf[String]
    workbook.getSheet(sheetName)

  }

  def extractArea(task: Task)(sheet: Sheet): SheetArea = {
    SheetArea(sheet, task("area").asInstanceOf[SheetRange])
  }

  def getCellValue(cell: Cell): Any = {
    try {
      cell.getCellType match {
        case Cell.CELL_TYPE_STRING => cell.getRichStringCellValue.getString
        case Cell.CELL_TYPE_NUMERIC => if (DateUtil.isCellDateFormatted(cell)) cell.getDateCellValue else cell.getNumericCellValue
        case Cell.CELL_TYPE_BOOLEAN => cell.getBooleanCellValue
        case Cell.CELL_TYPE_BLANK => ""
        case Cell.CELL_TYPE_FORMULA => cell.getCachedFormulaResultType match {
          case Cell.CELL_TYPE_STRING => cell.getRichStringCellValue.getString
          case Cell.CELL_TYPE_NUMERIC => if (DateUtil.isCellDateFormatted(cell)) cell.getDateCellValue else cell.getNumericCellValue
          case Cell.CELL_TYPE_BOOLEAN => cell.getBooleanCellValue
          case _ => throw new IllegalArgumentException(s"Can't get value of cell of type ${cell.getCellType}")
        }
        case _ => throw new IllegalArgumentException(s"Can't get value of cell of type ${cell.getCellType}")
      }
    } catch {
      case _: Exception => null
    }
  }

  def extractAttributes(task: Task)(sheetArea: SheetArea): (SheetArea, List[Attribute]) = {
    val attributes = task("attributeLabels") match {
      case rowNumber: RowNumber =>
        sheetArea.sheet.getRow(rowNumber.n).cellIterator.asScala.map(cell => {
          (
            getCellValue(cell).toString,
            (cellCoordinate: CellCoordinate) => {
              val valueCell = sheetArea.sheet.getRow(cellCoordinate._2).getCell(cell.getColumnIndex)
              if (valueCell != null) {
                getCellValue(valueCell)
              } else {
                null
              }
            }
          )
        }).toList
      case n: ColumnNumber => ???
    }
    (sheetArea, attributes)
  }

  def extractRecords(task: Task)(recipe: (SheetArea, List[Attribute])): List[Record] = {
    val recordKeys = task("recordKeys") match {
      case columnNumber: ColumnNumber => {
        recipe._1.sheet.rowIterator.asScala
          .drop(task("attributeLabels").asInstanceOf[RowNumber].n + 1)
          .map(row => row.getCell(columnNumber.n))
          .filterNot(cell => getCellValue(cell) == null || getCellValue(cell) == "")
          .toList
      }
      case n: RowNumber => ???
    }

    recordKeys.map(cell => {
      recipe._2.map(attribute => (attribute._1, attribute._2(new CellCoordinate(cell.getColumnIndex, cell.getRowIndex)))).toMap
    })
  }

  val res = extractRecords(task)(extractAttributes(task)(extractArea(task)(extractSheet(task)(workbook))))

  println(res)

//  Function.chain(
//    extractSheet(task), // Workbook -> Sheet
//    extractArea(task), // Sheet -> Area
//    extractAttributes(task), // Area -> (Sheet, Attributes)
//    extractRecords(task) // (Sheet, Attributes) -> Records
//  )
}
