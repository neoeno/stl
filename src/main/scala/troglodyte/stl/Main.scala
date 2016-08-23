package troglodyte.stl

import java.io.File

import org.apache.poi.ss.usermodel._
import troglodyte.stl.Operators._

object Main extends App {
  new Task("Violence", "A4:A101",
    List(
      Transformer(
        keyFn = fixedString("Date"),
        valueFn = column(0).andThen(value)
      ),
      Transformer(
        keyFn = columnHeading("B3", "VAP Offences"),
        valueFn = column(1).andThen(value)
      ),
      Transformer(
        keyFn = columnHeading("C3", "Violence with injury (VWI)"),
        valueFn = column(2).andThen(value)
      )
    )
  ).run(WorkbookFactory.create(new File("examples/1/mps-figures.xls"))).foreach(println(_))
}
