package troglodyte.stl

import org.apache.poi.ss.usermodel.{Cell, DateUtil, Sheet, Workbook}
import org.apache.poi.ss.util.CellRangeAddress

import scala.collection.JavaConverters._

object Spreadsheet {
  case class CellParsingException(message: String) extends Exception(message)
  type Task = Map[String, Any]

  def getSheet(workbook: Workbook)(sheetName: String): Option[Sheet] = {
    Option(workbook.getSheet(sheetName))
  }

  def cellsInRange(sheet: Sheet)(rangeString: String): Iterator[Cell] = {
    val range = CellRangeAddress.valueOf(rangeString)
    sheet.rowIterator.asScala
      .flatMap(row => row.cellIterator.asScala)
      .filter(cell => range.isInRange(cell.getRowIndex, cell.getColumnIndex))
  }

  def getCellValue(cell: Cell): Option[Any] = {
    Option(cell.getCellType match {
      case Cell.CELL_TYPE_STRING => cell.getRichStringCellValue.getString
      case Cell.CELL_TYPE_NUMERIC => if (DateUtil.isCellDateFormatted(cell)) cell.getDateCellValue else cell.getNumericCellValue
      case Cell.CELL_TYPE_BOOLEAN => cell.getBooleanCellValue
      case Cell.CELL_TYPE_BLANK => null
      // No real spreadsheet should have an error without a formula, but for completeness...
      case Cell.CELL_TYPE_ERROR => throw CellParsingException(s"Cell at ${cell.getAddress.toString} contains an error, cannot be evaluated")
      case Cell.CELL_TYPE_FORMULA => cell.getCachedFormulaResultType match {
        case Cell.CELL_TYPE_STRING => cell.getRichStringCellValue.getString
        case Cell.CELL_TYPE_NUMERIC => if (DateUtil.isCellDateFormatted(cell)) cell.getDateCellValue else cell.getNumericCellValue
        case Cell.CELL_TYPE_BOOLEAN => cell.getBooleanCellValue
        // Blank can't be returned here, so omitted
        case Cell.CELL_TYPE_ERROR => throw CellParsingException(s"Cell at ${cell.getAddress.toString} contains an error, cannot be evaluated")
      }
    })
  }
}
