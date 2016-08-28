package troglodyte.stl

import org.apache.poi.ss.usermodel.Cell
import org.scalatest.FunSpec

import scala.collection.JavaConverters._

class SpreadshooterTest extends FunSpec {
  describe("using Scala collections") {
    describe("given a simple sheet & task") {
      val spreadshooter = new Spreadshooter()
      spreadshooter.setWorkbook(TestFactory.makeWorkbook("sheet1")(List("col1", "col2"), List(1, 2), List(3, 4)))
      spreadshooter.setTask("sheet1", "A2:A3", List(
        Transformer[Cell](
          keyFn   = _ => "col1",
          valueFn = Operators.column(0).andThen(Operators.value)
        ),
        Transformer[Cell](
          keyFn   = _ => "col2",
          valueFn = Operators.column(1).andThen(Operators.value)
        )
      ))

      it("extracts the records from a workbook") {
        assert(
          spreadshooter.export().toList ==
            List(Map("col1" -> 1, "col2" -> 2), Map("col1" -> 3, "col2" -> 4)))
      }
    }
  }

  describe("using Java collections") {
    describe("given a simple sheet & task") {
      val spreadshooter = new Spreadshooter()
      spreadshooter.setWorkbook(TestFactory.makeWorkbook("sheet1")(List("col1", "col2"), List(1, 2), List(3, 4)))
      spreadshooter.setTask("sheet1", "A2:A3", List(
        Transformer[Cell](
          keyFn   = _ => "col1",
          valueFn = Operators.column(0).andThen(Operators.value)
        ),
        Transformer[Cell](
          keyFn   = _ => "col2",
          valueFn = Operators.column(1).andThen(Operators.value)
        )
      ).asJava)

      it("extracts the records from a workbook") {
        assert(
          spreadshooter.exportAsJava().asScala.map(_.asScala).toList ==
            List(Map("col1" -> 1, "col2" -> 2), Map("col1" -> 3, "col2" -> 4)))
      }
    }
  }
}
