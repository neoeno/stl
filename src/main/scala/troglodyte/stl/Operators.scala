package troglodyte.stl

import org.apache.poi.ss.usermodel.{Cell, Sheet}
import org.apache.poi.ss.util.CellAddress

object Operators {
  case class SpreadsheetAssertionException(message: String) extends Exception(message)

  def fixedString(value: String) = (cell: Cell) => value

  def columnHeading(cellAddress: String, label: String) = (cell: Cell) => {
    if (!valueOfCellAtAddress(cellAddress, cell.getSheet).contains(label)) {
      throw new SpreadsheetAssertionException(s"Column heading cell at $cellAddress did not match given value '$label'")
    }
    label
  }

  def column(colNum: Int) = (cell: Cell) =>
    Option(cell.getRow.getCell(colNum))

  def valueOr(alternative: Any) = (cell: Option[Cell]) => cell.flatMap(Spreadsheet.getCellValue).getOrElse(alternative)

  def value = valueOr(null)

  private def valueOfCellAtAddress(cellAddressString: String, sheet: Sheet): Option[Any] = {
    val cellAddress = new CellAddress(cellAddressString)
    valueOfCellAtCoord(cellAddress.getRow, cellAddress.getColumn, sheet)
  }

  private def valueOfCellAtCoord(rowNum: Int, colNum: Int, sheet: Sheet): Option[Any] = {
    val row = sheet.getRow(rowNum)
    if (row != null) {
      val cell = row.getCell(colNum)
      if (cell != null) {
        Spreadsheet.getCellValue(cell)
      } else {
        None
      }
    } else {
      None
    }
  }
}
