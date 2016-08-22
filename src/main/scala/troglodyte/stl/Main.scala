package troglodyte.stl

import java.io.File

import org.apache.poi.ss.usermodel._
import troglodyte.stl.Operators._
import troglodyte.stl.Translator.Transformer

object Main extends App {
  val task = Map(
    "sheet" -> "Violence",
    "keyCells" -> "A4:A101",
    "attributes" -> List(
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
  )

  val workbook = WorkbookFactory.create(new File("examples/1/mps-figures.xls"))

  Translator.extractRecordsByTask(task)(workbook).foreach(println(_))
}
