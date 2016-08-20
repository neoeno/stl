package troglodyte.stl

import org.apache.poi.ss.usermodel.{Cell, DateUtil, Sheet, Workbook}
import org.apache.poi.ss.util.CellRangeAddress

import scala.collection.JavaConverters._

object Spreadsheet {
  type Task = Map[String, Any]

  def getSheet(workbook: Workbook)(sheetName: String): Sheet = {
    workbook.getSheet(sheetName)
  }

  def cellsInRange(sheet: Sheet)(rangeString: String): Iterator[Cell] = {
    val range = CellRangeAddress.valueOf(rangeString)
    sheet.rowIterator.asScala
      .flatMap(row => row.cellIterator.asScala)
      .filter(cell => range.isInRange(cell.getRowIndex, cell.getColumnIndex))
  }

  // This belongs somewhere else
  def constructMapWithFns[T](keyFns: List[T => String], valueFns: List[T => Any])(item: T): Map[String, Any] = {
    val keys = keyFns.map(fn => fn(item))
    val values = valueFns.map(fn => fn(item))
    keys.zip(values).toMap
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
}
