package troglodyte.stl

import org.scalatest.FunSpec

class STLParser$Test extends FunSpec {
  describe(".parseToTask") {
    it ("parses an STL string to a task") {
      val stl =
        """
          |NAME "VAP Offences By Month"
          |SHEET "Violence"
        """.stripMargin
      assert(STLParser.parseToTask(stl) == Map("name" -> "VAP Offences By Month", "sheet" -> "Violence"))
    }
  }
}
