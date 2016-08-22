package troglodyte.stl

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.{Cell, Workbook}

object TestFactory {
  case class Formula(formula: String)

  def makeWorkbook(sheetName: String)(rowSpecs: List[Any]*): Workbook = {
    val workbook = new HSSFWorkbook()
    val sheet = workbook.createSheet(sheetName)
    rowSpecs.zipWithIndex.foreach { case (rowSpec, rowIdx) =>
      val row = sheet.createRow(rowIdx)
      rowSpec.zipWithIndex.foreach { case (cellSpec, cellIdx) =>
        cellSpec match {
          case value: Int => row.createCell(cellIdx).setCellValue(value)
          case value: String => row.createCell(cellIdx).setCellValue(value)
          case value: Double => row.createCell(cellIdx).setCellValue(value)
          case value: Boolean => row.createCell(cellIdx).setCellValue(value)
          case value: Formula =>
            val cell = row.createCell(cellIdx)
            cell.setCellFormula(value.formula)
            workbook.getCreationHelper.createFormulaEvaluator().evaluateFormulaCell(cell)
          case null => row.createCell(cellIdx).setCellType(Cell.CELL_TYPE_BLANK)
        }

      }
    }
    workbook
  }

  def makeFormula(formula: String): Formula = {
    Formula(formula)
  }
}
