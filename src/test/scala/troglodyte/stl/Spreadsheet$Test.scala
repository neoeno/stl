package troglodyte.stl

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.scalatest.FunSpec

class Spreadsheet$Test extends FunSpec {
  describe("given a simple Excel file") {
    val workbook = new HSSFWorkbook()
    val sheet = workbook.createSheet("sheet1")
    val headingRow = sheet.createRow(0)
    headingRow.createCell(0).setCellValue("col1")
    headingRow.createCell(1).setCellValue("col2")
    val valueRow1 = sheet.createRow(1)
    valueRow1.createCell(0).setCellValue(1)
    valueRow1.createCell(1).setCellValue(2)
    val valueRow2 = sheet.createRow(2)
    valueRow2.createCell(0).setCellValue(3)
    valueRow2.createCell(1).setCellValue(4)

    describe(".getSheet") {
      describe("given an extant sheet name") {
        it ("returns that sheet") {
          assert(Spreadsheet.getSheet(workbook)("sheet1").getSheetName == "sheet1")
        }
      }

      describe("given a non extant sheet name") {
        it ("returns null") {
          assert(Spreadsheet.getSheet(workbook)("sheet_that_does_not_exist") == null)
        }
      }
    }

    describe(".cellsInRange") {
      describe("given a single column range") {
        it ("returns an iterator with the cells in that range") {
          val values = Spreadsheet.cellsInRange(sheet)("A1:A2").map(Spreadsheet.getCellValue).toList
          assert(values == List("col1", 1))
        }
      }

      describe("given a single row range") {
        it ("returns an iterator with the cells in that range") {
          val values = Spreadsheet.cellsInRange(sheet)("A1:B1").map(Spreadsheet.getCellValue).toList
          assert(values == List("col1", "col2"))
        }
      }

      describe("given a multi-row multi-column range") {
        it ("returns an iterator with the cells in that range") {
          val values = Spreadsheet.cellsInRange(sheet)("A1:B2").map(Spreadsheet.getCellValue).toList
          assert(values == List("col1", "col2", 1, 2))
        }
      }
    }

    describe(".getCellValue") {
      // left untested because i'm lazy
    }
  }

  describe(".constructMapWithFunctions") {
    it ("returns a map composed by zipping the returns of the keyFns and valueFns over the item") {
      val constructor = Spreadsheet.constructMapWithFns[Int](List(
        n => (n + 1).toString,
        _ => "hello!"
      ), List(
        n => n + 2,
        n => "world!" + n
      ))(_)
      assert(constructor(1) == Map("2" -> 3, "hello!" -> "world!1"))
    }
  }
}
