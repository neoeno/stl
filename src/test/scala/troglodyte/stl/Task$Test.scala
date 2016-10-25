package troglodyte.stl

import org.apache.poi.ss.usermodel.Cell
import org.scalatest.FunSpec

class Task$Test extends FunSpec {
  describe(".exportWorkbookToRecords") {
    describe("given a simple sheet & task") {
      val workbook = TestFactory.makeWorkbook("sheet1")(List("col1", "col2"), List(1, 2), List(3, 4))
      val simpleTask = new Task("sheet1", "A2:A3", List(
          Extractor[Cell](
            keyFn   = _ => "col1",
            valueFn = Operators.column(0).andThen(Operators.value)
          ),
          Extractor[Cell](
            keyFn   = _ => "col2",
            valueFn = Operators.column(1).andThen(Operators.value)
          )
        )
      )

      it("extracts the records from a workbook") {
        assert(
          simpleTask.exportWorkbookToRecords(workbook).toList ==
            List(Map("col1" -> 1, "col2" -> 2), Map("col1" -> 3, "col2" -> 4)))
      }
    }
  }

  describe(".exportWorkbookToRecords") {
    describe("given a sheet and task with some gaps") {
      val workbook = TestFactory.makeWorkbook("sheet1")(
        List("col1", "col2"),
        List("hello", "world"),
        List(1, null),
        List(3)
      )
      val simpleTask = new Task("sheet1", "A2:A4", List(
          Extractor[Cell](
            keyFn   = _ => "col1",
            valueFn = Operators.column(0).andThen(Operators.value)
          ),
          Extractor[Cell](
            keyFn   = _ => "col2",
            valueFn = Operators.column(1).andThen(Operators.value)
          )
        )
      )

      it("extracts the records from a workbook") {
        assert(
          simpleTask.exportWorkbookToRecords(workbook).toList ==
            List(
              Map("col1" -> "hello", "col2" -> "world"),
              Map("col1" -> 1, "col2" -> null),
              Map("col1" -> 3, "col2" -> null)
            )
        )
      }
    }
  }
}
