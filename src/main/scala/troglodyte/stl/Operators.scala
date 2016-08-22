package troglodyte.stl

import org.apache.poi.ss.usermodel.Row.MissingCellPolicy
import org.apache.poi.ss.usermodel.{Cell, Sheet}
import org.apache.poi.ss.util.CellAddress

object Operators {
  val fixedString = (value: String) => (cell: Cell) => value

  val columnHeading = (cellAddress: String, label: String) => (cell: Cell) => {
    if (!valueOfCellAtAddress(cellAddress, cell.getSheet).contains(label)) {
      throw new IllegalArgumentException(s"Column heading cell at $cellAddress did not match given value '$label'")
    }
    label
  }

  val column = (colNum: Int) => (cell: Cell) =>
    Option(cell.getRow.getCell(colNum))

  val valueOr = (alternative: Any) => (cell: Option[Cell]) => cell.flatMap(Spreadsheet.getCellValue).getOrElse(alternative)

  val value = valueOr(null)

  private val valueOfCellAtAddress = (cellAddressString: String, sheet: Sheet) => {
    val cellAddress = new CellAddress(cellAddressString)
    valueOfCellAtCoord(cellAddress.getRow, cellAddress.getColumn, sheet)
  }

  private val valueOfCellAtCoord = (rowNum: Int, colNum: Int, sheet: Sheet) => {
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
