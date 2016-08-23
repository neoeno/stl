package troglodyte.stl

import org.apache.poi.ss.usermodel.{Cell, Workbook}

class Task(sheetName: String, keyCellsAddressRange: String, columns: Seq[Transformer[Cell]]) {
  def run(workbook: Workbook): Iterator[Map[String, Any]] = {
    Spreadsheet.cellsInRange(
      Spreadsheet.getSheet(workbook)(sheetName).get
    )(keyCellsAddressRange).map(
      constructMapWithTransformers(columns)
    )
  }

  private def constructMapWithTransformers[T](transformers: Seq[Transformer[T]])(item: T): Map[String, Any] = {
    val keys = transformers.map(_.keyFn(item))
    val values = transformers.map(_.valueFn(item))
    keys.zip(values).toMap
  }
}
