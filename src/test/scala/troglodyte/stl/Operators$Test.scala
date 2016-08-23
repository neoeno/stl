package troglodyte.stl

import org.scalatest.FunSpec
import troglodyte.stl.Operators.SpreadsheetAssertionException

class Operators$Test extends FunSpec {
  val workbook = TestFactory.makeWorkbook("sheet1")(List("col1", "col2"), List(1, 2), List(3, 4), List(5))
  val sheet = workbook.getSheet("sheet1")

  describe(".fixedString") {
    it("returns the fixed string") {
      val cell = sheet.getRow(0).getCell(0)
      assert(Operators.fixedString("hello!")(cell) == "hello!")
    }
  }

  describe(".columnHeading") {
    describe("with a cell ref matching the label") {
      it("returns the label") {
        val cell = sheet.getRow(0).getCell(0)
        assert(Operators.columnHeading("A1", "col1")(cell) == "col1")
      }
    }

    describe("with a cell ref not matching the label") {
      it("throws an exception") {
        val cell = sheet.getRow(0).getCell(0)
        val caught = intercept[SpreadsheetAssertionException] {
          Operators.columnHeading("B1", "col1")(cell)
        }
        assert(caught.getMessage == "Column heading cell at B1 did not match given value 'col1'")
      }
    }

    describe("with a cell ref for a row that does not exist and a column that does not exist") {
      it("throws an exception") {
        val cell = sheet.getRow(0).getCell(0)
        val caught = intercept[SpreadsheetAssertionException] {
          Operators.columnHeading("Z40", "col1")(cell)
        }
        assert(caught.getMessage == "Column heading cell at Z40 did not match given value 'col1'")
      }
    }

    describe("with a cell ref for a cell that does not exist in a row that does exist") {
      it("throws an exception") {
        val cell = sheet.getRow(0).getCell(0)
        val caught = intercept[SpreadsheetAssertionException] {
          Operators.columnHeading("Z1", "col1")(cell)
        }
        assert(caught.getMessage == "Column heading cell at Z1 did not match given value 'col1'")
      }
    }
  }


  describe(".column") {
    describe("given a cell that exists") {
      it("returns the cell in the given cell's row and the provided column") {
        val cell = sheet.getRow(0).getCell(0)
        assert(Operators.column(0)(cell) == Some(sheet.getRow(0).getCell(0)))
        assert(Operators.column(1)(cell) == Some(sheet.getRow(0).getCell(1)))
      }
    }

    describe("given a cell that does not exist") {
      it("returns null") {
        val cell = sheet.getRow(3).getCell(0)
        assert(Operators.column(1)(cell) == None)
      }
    }
  }

  describe(".valueOr") {
    describe("given some value") {
      it("returns the value of the cell") {
        val cell = sheet.getRow(0).getCell(0)
        assert(Operators.valueOr("not this")(Some(cell)) == "col1")
      }
    }

    describe("given no value") {
      it("returns the alternative value") {
        val cell = sheet.getRow(0).getCell(0)
        assert(Operators.valueOr("this")(None) == "this")
      }
    }
  }

  describe(".value") {
    describe("given some value") {
      it("returns the value of the cell") {
        val cell = sheet.getRow(0).getCell(0)
        assert(Operators.value(Some(cell)) == "col1")
      }
    }

    describe("given no value") {
      it("returns null") {
        val cell = sheet.getRow(0).getCell(0)
        assert(Operators.value(None) == null)
      }
    }
  }
}
