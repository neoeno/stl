package troglodyte.stl

import org.scalatest.FunSpec
import troglodyte.stl.STLParser.{STLAddressRange, STLExpression, STLFunction}

class STLParser$Test extends FunSpec {
  describe(".parseToTask") {
    it ("parses an STL string to a task") {
      val stl =
        """
          |NAME "VAP Offences By Month"
          |SHEET "Violence"
          |KEY_CELLS [A4:A101]
          |
          |COLUMN fixedString("Date")
          |    => column(0) | value
          |COLUMN columnHeading("B3", "VAP Offences")
          |    => column(1) | value
        """.stripMargin

      // Equation stuff means this won't pass as a literal, we'll fix it later
      assert(STLParser.parseToTask(stl).toString == "Map(name -> VAP Offences By Month, sheet -> Violence, keyCells -> STLAddressRange(A4:A101), attributes -> Vector((STLExpression(Vector(STLFunction(fixedString,Vector(Date)))),STLExpression(Vector(STLFunction(column,Vector(0)), STLFunction(value,Vector())))), (STLExpression(Vector(STLFunction(columnHeading,Vector(B3, VAP Offences)))),STLExpression(Vector(STLFunction(column,Vector(1)), STLFunction(value,Vector()))))))")
    }
  }
}
