package troglodyte.stl

import org.apache.poi.ss.usermodel.{Cell, Workbook}

object Translator {
  type Task = Map[String, Any]

  def extractRecordsByTask(task: Task)(workbook: Workbook): Iterator[Map[String, Any]] = {
    val sheetName = task("sheet").asInstanceOf[String]
    val keyCellsRange = task("keyCells").asInstanceOf[String]
    val attributeNameFns = task("attributeNameFns").asInstanceOf[List[Cell => String]]
    val attributeValueFns = task("attributeValueFns").asInstanceOf[List[Cell => Any]]

    Spreadsheet.cellsInRange(
      Spreadsheet.getSheet(workbook)(sheetName)
    )(keyCellsRange).map(
      Spreadsheet.constructMapWithFns(attributeNameFns, attributeValueFns)
    )
  }
}
