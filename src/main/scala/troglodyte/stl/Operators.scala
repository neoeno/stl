package troglodyte.stl

import org.apache.poi.ss.usermodel.Row.MissingCellPolicy
import org.apache.poi.ss.usermodel.{Cell, Sheet}
import org.apache.poi.ss.util.CellAddress

object Operators {
  val fixedString = (value: String) => (cell: Cell) => value

  val columnHeading = (cellAddress: String, label: String) => (cell: Cell) => {
    if (label != valueOfCellAtAddress(cellAddress, cell.getSheet)) {
      throw new IllegalArgumentException(s"Column heading cell at $cellAddress did not match given value '$label'")
    }
    label
  }

  val column = (colNum: Int) => (cell: Cell) =>
    cell.getRow.getCell(colNum, MissingCellPolicy.RETURN_BLANK_AS_NULL)

  val value = (cell: Cell) => Spreadsheet.getCellValue(cell)

  private val valueOfCellAtAddress = (cellAddressString: String, sheet: Sheet) => {
    val cellAddress = new CellAddress(cellAddressString)
    valueOfCellAtCoord(cellAddress.getRow, cellAddress.getColumn, sheet).orNull
  }

  private val valueOfCellAtCoord = (rowNum: Int, colNum: Int, sheet: Sheet) => {
    val row = sheet.getRow(rowNum)
    if (row != null) {
      val cell = row.getCell(colNum, MissingCellPolicy.RETURN_BLANK_AS_NULL)
      if (cell != null) {
        Some(value(cell))
      } else {
        None
      }
    } else {
      None
    }
  }
}
