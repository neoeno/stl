package troglodyte.stl

import org.scalatest.FunSpec

class Operators$Test extends FunSpec {
  val workbook = TestFactory.makeWorkbook("sheet1")(List("col1", "col2"), List(1, 2), List(3, 4), List(5))
  val sheet = workbook.getSheet("sheet1")

  describe(".fixedString") {
    it ("returns the fixed string") {
      val cell = sheet.getRow(0).getCell(0)
      assert(Operators.fixedString("hello!")(cell) == "hello!")
    }
  }

  describe(".columnHeading") {
    describe("with a cell ref matching the label") {
      it ("returns the label") {
        val cell = sheet.getRow(0).getCell(0)
        assert(Operators.columnHeading("A1", "col1")(cell) == "col1")
      }
    }

    describe("with a cell ref not matching the label") {
      it ("throws an exception") {
        val cell = sheet.getRow(0).getCell(0)
        val caught = intercept[IllegalArgumentException] {
          Operators.columnHeading("B1", "col1")(cell)
        }
        assert(caught.getMessage == "Column heading cell at B1 did not match given value 'col1'")
      }
    }

    describe("with a cell ref for a row that does not exist and a column that does not exist") {
      it ("throws an exception") {
        val cell = sheet.getRow(0).getCell(0)
        val caught = intercept[IllegalArgumentException] {
          Operators.columnHeading("Z40", "col1")(cell)
        }
        assert(caught.getMessage == "Column heading cell at Z40 did not match given value 'col1'")
      }
    }

    describe("with a cell ref for a cell that does not exist in a row that does exist") {
      it ("throws an exception") {
        val cell = sheet.getRow(0).getCell(0)
        val caught = intercept[IllegalArgumentException] {
          Operators.columnHeading("A40", "col1")(cell)
        }
        assert(caught.getMessage == "Column heading cell at A40 did not match given value 'col1'")
      }
    }
  }


  describe(".column") {
    describe("given a cell that exists") {
      it("returns the cell in the given cell's row and the provided column") {
        val cell = sheet.getRow(0).getCell(0)
        assert(Operators.column(0)(cell) == sheet.getRow(0).getCell(0))
        assert(Operators.column(1)(cell) == sheet.getRow(0).getCell(1))
      }
    }

    describe("given a cell that does not exist") {
      it("returns null") {
        val cell = sheet.getRow(3).getCell(0)
        assert(Operators.column(1)(cell) == null)
      }
    }
  }

  describe(".value") {
    it ("returns the value of the cell") {
      val cell = sheet.getRow(0).getCell(0)
      assert(Operators.value(cell) == "col1")
    }
  }
}
