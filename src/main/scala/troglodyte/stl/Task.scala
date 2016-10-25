package troglodyte.stl

import org.apache.poi.ss.usermodel.{Cell, Workbook}

/* Task
 * Represents a specific extraction task. For instance, if you have a spreadsheet
 * with four tables in all of which you want to extract data from, you'll execute
 * four tasks.
 */
class Task(sheetName: String, keyCellsAddressRange: String, columns: Seq[Extractor[Cell]]) {
  def exportWorkbookToRecords(workbook: Workbook): Iterator[Map[String, Any]] = {
    val sheet = Spreadsheet.getSheet(workbook)(sheetName).get
    val keyCells = Spreadsheet.cellsInRange(sheet)(keyCellsAddressRange)
    keyCells.map(
      constructMapWithExtractors(columns)
    )
  }

  // Obnoxiously generic method to apply an item to a list of extractors in turn
  private def constructMapWithExtractors[T](extractors: Seq[Extractor[T]])(item: T): Map[String, Any] = {
    val keys = extractors.map(_.keyFn(item))
    val values = extractors.map(_.valueFn(item))
    keys.zip(values).toMap
  }
}
