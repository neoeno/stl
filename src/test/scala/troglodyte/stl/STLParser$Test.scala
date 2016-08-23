package troglodyte.stl

import org.scalatest.FunSpec

class STLParser$Test extends FunSpec {
  val workbook = TestFactory.makeWorkbook("sheet1")(List("col1", "col2"), List(1, 2), List(3, 4))

  describe(".parseToTask") {
    it ("parses an STL string to a task") {
      val stl =
        """
          |NAME "A Test"
          |SHEET "sheet1"
          |KEY_CELLS [A2:A3]
          |
          |COLUMN fixedString("col1")
          |    => column(0) | value
          |COLUMN fixedString("col2")
          |    => column(1) | value
        """.stripMargin

      val records = Translator.extractRecordsByTask(STLParser.parseToTask(stl))(workbook).toList
      assert(records == List(Map("col1" -> 1, "col2" -> 2), Map("col1" -> 3, "col2" -> 4)))
    }
  }
}
