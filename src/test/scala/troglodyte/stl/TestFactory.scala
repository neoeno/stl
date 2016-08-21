package troglodyte.stl

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Workbook

object TestFactory {
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
        }

      }
    }
    workbook
  }

}
