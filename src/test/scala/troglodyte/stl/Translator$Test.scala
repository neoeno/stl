package troglodyte.stl

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy
import org.scalatest.FunSpec
import troglodyte.stl.Translator.Transformer

class Translator$Test extends FunSpec {
  val workbook = TestFactory.makeWorkbook("sheet1")(List("col1", "col2"), List(1, 2), List(3, 4))

  describe(".extractRecordsByTask") {
    describe("given a simple task") {
      val simpleTask = Map(
        "sheet" -> "sheet1",
        "keyCells" -> "A2:A3",
        "attributes" -> List(
          Transformer[Cell](
            keyFn   = _ => "col1",
            valueFn = cell => Spreadsheet.getCellValue(cell)
          ),
          Transformer[Cell](
            keyFn   = _ => "col2",
            valueFn = cell => Spreadsheet.getCellValue(cell.getRow.getCell(1, MissingCellPolicy.RETURN_BLANK_AS_NULL))
          )
        )
      )

      it ("extracts the records from a workbook") {
        assert(
          Translator.extractRecordsByTask(simpleTask)(workbook).toList ==
            List(Map("col1" -> 1, "col2" -> 2), Map("col1" -> 3, "col2" -> 4)))
      }
    }
  }

  describe(".constructMapWithFunctions") {
    it ("returns a map composed by zipping the returns of the keyFns and valueFns over the item") {
      val constructor = Translator.constructMapWithTransformers[Int](List(
        Transformer(
          keyFn   = n => (n + 1).toString,
          valueFn = n => n + 2
        ),
        Transformer(
          keyFn   = _ => "hello!",
          valueFn = n => "world!" + n
        )
      ))(_)
      assert(constructor(1) == Map("2" -> 3, "hello!" -> "world!1"))
    }
  }
}
