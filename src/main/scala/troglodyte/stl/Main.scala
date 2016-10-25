package troglodyte.stl

import java.io.File

import org.apache.poi.ss.usermodel._
import troglodyte.stl.Operators._

object Main extends App {
  val workbook = WorkbookFactory.create(new File("examples/1/mps-figures.xls"))

  val task = new Task("Violence", "A4:A101",
    List(
      Extractor(
        keyFn = fixedString("Date"),
        valueFn = column(0).andThen(value)
      ),
      Extractor(
        keyFn = columnHeading("B3", "VAP Offences"),
        valueFn = column(1).andThen(value)
      ),
      Extractor(
        keyFn = columnHeading("C3", "Violence with injury (VWI)"),
        valueFn = column(2).andThen(value)
      )
    )
  )

  task.exportWorkbookToRecords(workbook).foreach(println(_))
}
