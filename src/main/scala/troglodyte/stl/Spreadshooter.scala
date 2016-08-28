package troglodyte.stl

import java.io.{File, InputStream}
import java.util

import org.apache.poi.ss.usermodel.{Cell, Workbook, WorkbookFactory}

import scala.collection.JavaConverters._

class Spreadshooter {
  var task: Task = _
  var workbook: Workbook = _

  def setTask(sheetName: String, keyCellsAddressRange: String, columns: Seq[Transformer[Cell]]): Unit = {
    task = new Task(sheetName, keyCellsAddressRange, columns)
  }

  def setTask(sheetName: String, keyCellsAddressRange: String, columns: java.util.List[Transformer[Cell]]): Unit = {
    task = new Task(sheetName, keyCellsAddressRange, columns.asScala)
  }

  def setWorkbook(file: File): Unit = {
    workbook = WorkbookFactory.create(file)
  }

  def setWorkbook(inputStream: InputStream): Unit = {
    workbook = WorkbookFactory.create(inputStream)
  }

  def setWorkbook(workbook: Workbook): Unit = {
    this.workbook = workbook
  }

  def exportAsJava(): util.Iterator[util.Map[String, AnyRef]] = {
    task.exportWorkbookToRecords(workbook).map(_.mapValues(_.asInstanceOf[AnyRef]).asJava).asJava
  }

  def export(): Iterator[Map[String, Any]] = {
    task.exportWorkbookToRecords(workbook)
  }
}
