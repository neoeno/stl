package troglodyte.stl

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy
import org.scalatest.FunSpec

class Translator$Test extends FunSpec {
  val workbook = TestFactory.makeWorkbook("sheet1")(List("col1", "col2"), List(1, 2), List(3, 4))

  describe(".extractRecordsByTask") {
    describe("given a simple task") {
      val simpleTask = Map(
        "sheet" -> "sheet1",
        "keyCells" -> "A2:A3",
        "attributeNameFns" -> List(
          (cell: Cell) => "col1",
          (cell: Cell) => "col2"
        ),
        "attributeValueFns" -> List(
          (cell: Cell) => Spreadsheet.getCellValue(cell),
          (cell: Cell) => Spreadsheet.getCellValue(cell.getRow.getCell(1, MissingCellPolicy.RETURN_BLANK_AS_NULL))
        )
      )

      it ("extracts the records from a workbook") {
        assert(
          Translator.extractRecordsByTask(simpleTask)(workbook).toList ==
            List(Map("col1" -> 1, "col2" -> 2), Map("col1" -> 3, "col2" -> 4)))
      }
    }
  }
}
