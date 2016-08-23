package troglodyte.stl

import org.scalatest.FunSpec
import troglodyte.stl.Spreadsheet.CellParsingException

class Spreadsheet$Test extends FunSpec {
  describe("given a simple Excel file") {
    val workbook = TestFactory.makeWorkbook("sheet1")(
      List("col1", "col2"),
      List(1,      2),
      List(3,      4),
      List(1.5,    false,  null),
      List(
        TestFactory.makeFormula("LEFT(\"hello\", 1)"),
        TestFactory.makeFormula("A2+2.5"),
        TestFactory.makeFormula("AND(True, False)"),
        TestFactory.makeFormula("1/0")
      )
    )
    val sheet = workbook.getSheet("sheet1")

    describe(".getSheet") {
      describe("given an extant sheet name") {
        it("returns that sheet") {
          assert(Spreadsheet.getSheet(workbook)("sheet1").get.getSheetName == "sheet1")
        }
      }

      describe("given a non extant sheet name") {
        it("returns null") {
          assert(Spreadsheet.getSheet(workbook)("sheet_that_does_not_exist") == None)
        }
      }
    }

    describe(".cellsInRange") {
      describe("given a single column range") {
        it("returns an iterator with the cells in that range") {
          val values = Spreadsheet.cellsInRange(sheet)("A1:A2").map(Spreadsheet.getCellValue).map(_.get).toList
          assert(values == List("col1", 1))
        }
      }

      describe("given a single row range") {
        it("returns an iterator with the cells in that range") {
          val values = Spreadsheet.cellsInRange(sheet)("A1:B1").map(Spreadsheet.getCellValue).map(_.get).toList
          assert(values == List("col1", "col2"))
        }
      }

      describe("given a multi-row multi-column range") {
        it("returns an iterator with the cells in that range") {
          val values = Spreadsheet.cellsInRange(sheet)("A1:B2").map(Spreadsheet.getCellValue).map(_.get).toList
          assert(values == List("col1", "col2", 1, 2))
        }
      }
    }

    describe(".getCellValue") {
      describe("given a string cell") {
        it("returns the string value") {
          assert(Spreadsheet.getCellValue(sheet.getRow(0).getCell(0)).get.toString == "col1")
        }
      }

      describe("given a numeric cell") {
        it("returns the numeric value") {
          assert(Spreadsheet.getCellValue(sheet.getRow(3).getCell(0)).get.toString == "1.5")
        }
      }

      describe("given a boolean cell") {
        it("returns the boolean value") {
          assert(Spreadsheet.getCellValue(sheet.getRow(3).getCell(1)).get.toString == "false")
        }
      }

      describe("given a blank cell") {
        it("returns null") {
          assert(Spreadsheet.getCellValue(sheet.getRow(3).getCell(2)) == None)
        }
      }

      describe("given a string formula cell") {
        it("returns the cached value") {
          assert(Spreadsheet.getCellValue(sheet.getRow(4).getCell(0)).get.toString == "h")
        }
      }

      describe("given a numeric formula cell") {
        it("returns the cached value") {
          assert(Spreadsheet.getCellValue(sheet.getRow(4).getCell(1)).get.toString == "3.5")
        }
      }

      describe("given a boolean formula cell") {
        it("returns the cached value") {
          assert(Spreadsheet.getCellValue(sheet.getRow(4).getCell(2)).get.toString == "false")
        }
      }

      describe("given an erroneous formula cell") {
        it("throws a CellParsingException") {
          val caught = intercept[CellParsingException] {
            Spreadsheet.getCellValue(sheet.getRow(4).getCell(3))
          }
          assert(caught.message == "Cell at D5 contains an error, cannot be evaluated")
        }
      }
    }
  }
}
