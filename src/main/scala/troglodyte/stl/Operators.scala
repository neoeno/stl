package troglodyte.stl

import org.apache.poi.ss.usermodel.{Cell, Sheet}
import org.apache.poi.ss.util.CellAddress

/* Operators
 *
 * Conceptually, these are higher order functions to generate small composable functions we use
 * to create complex operations on spreadsheets. They're the engine of our DSL. You'll see them
 * used like this:
 *
 *   column(5).andThen(getValueOr("default"))
 *   // Get the cell in column 5, and return its value or the default "default" if it isn't present
 */
object Operators {
  case class SpreadsheetAssertionException(message: String) extends Exception(message)

  // Ignore the cell, return a fixed string. Handy!
  def fixedString(value: String) = (cell: Cell) => value

  // Checks that label exists in cellAddress and returns label if so, throws otherwise
  def columnHeading(cellAddress: String, label: String) = (cell: Cell) => {
    if (!valueOfCellAtAddress(cellAddress, cell.getSheet).contains(label)) {
      throw SpreadsheetAssertionException(s"Column heading cell at $cellAddress did not match given value '$label'")
    }
    label
  }

  // Get the cell in the same row and the given column
  // returns None if the cell doesn't exist
  def column(colNum: Int) = (cell: Cell) =>
    Option(cell.getRow.getCell(colNum))

  // Get the value of a cell or a an alternative if it isn't available
  def valueOr(alternative: Any) = (cell: Option[Cell]) =>
    cell.flatMap(Spreadsheet.getCellValue).getOrElse(alternative)

  // Get the value of a cell or null if it isn't available
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
