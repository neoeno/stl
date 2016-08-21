package troglodyte.stl

import org.apache.poi.ss.usermodel.{Cell, Workbook}

object Translator {
  type Task = Map[String, Any]
  case class Transformer[T](keyFn: T => String, valueFn: T => Any)

  // This belongs somewhere else
  def constructMapWithTransformers[T](transformers: List[Transformer[T]])(item: T): Map[String, Any] = {
    val keys = transformers.map(_.keyFn(item))
    val values = transformers.map(_.valueFn(item))
    keys.zip(values).toMap
  }

  def extractRecordsByTask(task: Task)(workbook: Workbook): Iterator[Map[String, Any]] = {
    val sheetName = task("sheet").asInstanceOf[String]
    val keyCellsRange = task("keyCells").asInstanceOf[String]
    val attributes = task("attributes").asInstanceOf[List[Transformer[Cell]]]

    Spreadsheet.cellsInRange(
      Spreadsheet.getSheet(workbook)(sheetName)
    )(keyCellsRange).map(
      constructMapWithTransformers(attributes)
    )
  }
}
