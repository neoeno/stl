# STL: Spreadsheet Translation Language

Towards creating a simple DSL for converting (Excel) spreadsheets to nicer formats.

## Idea

Imagine your task is to convert a spreadsheet to a list of records. So you start off with this:

```
|-------|-------|
|  name |  size |
|-------|-------|
| rhino |   big |
|-------|-------|
|   cat | small |
|-------|-------|
```

And end up with this:

```json
[
  {
    col1: "hello",
    col2: "world"
  },
  {
    col1: "cat",
    col2: "dog"
  }
]
```

Sure, you could yank the header row and then iterate over the rest, but imagine if you had
to make the same tool deal with spreadsheets like these, while producing the same output:

```
|-------|-------|
|       |       |
|-------|-------|
|  name |  size |
|-------|-------|
| rhino |   big |
|-------|-------|
|   cat | small |
|-------|-------|
```

```
|-------|-------|-------|
|       |       |       |
|-------|-------|-------|
|  name | rhino |   cat |
|-------|-------|-------|
|  size |   big | small |
|-------|-------|-------|
```

The idea here is that you can reimagine spreadsheet data extraction according to the
following idea:

To extract data from a spreadsheet you define a set of Key Cells. In this spreadsheet
we might pick the Key Cell range is `A2:A3`. It doesn't matter which, as long as there
is one Key Cell in every row we want to extract.

```
      A       B
  |-------|-------|
1 |  name |  size |
  |-------|-------|
2 | rhino |   big |
  |-------|-------|
3 |   cat | small |
  |-------|-------|
```

You then define a set of functions that take a Cell and return a Value (e.g. a string),
for example:

```scala
def extractName(cell: Cell) => String = {
  val sheet = cell.getSheet() // Get the sheet our cell is in
  sheet
      .getRow(cell.getRow())  // Select the row our cell is in
      .getCell("A")           // Column 'A' contains the name, so pick that one
      .getStringValue()       // Get the value
}

def extractSize(cell: Cell) => String = {
  val sheet = cell.getSheet()
  sheet
      .getRow(cell.getRow())
      .getCell("B")           // Here, column B contains size, so that one instead
      .getStringValue()
}

val extractors = [extractName, extractSize]
```

These functions do the equivalent of picking a row and then running your finger along

Then we can iterate over our Key Cells, and in turn our functions, and end up with our data:

```scala
getKeyCells("A2:A3").map(cell => {
  extractors.map(extractor => {
    extractor(cell)
  })
})

// Results in:
// [
//   ["rhino", "big"],
//   ["cat", "small"]
// ]
```

It's trivial to extend this to the offset rotated table by changing the extractor functions:

```
      A       B       C
  |-------|-------|-------|
1 |       |       |       |
  |-------|-------|-------|
2 |  name | rhino |   cat |
  |-------|-------|-------|
3 |  size |   big | small |
  |-------|-------|-------|
```

```scala
// Key Cells are B2:C2

def extractName(cell: Cell) => String = {
  val sheet = cell.getSheet()
  sheet
      .getRow(2)
      .getCell(cell.getColumn())
      .getStringValue()
}

def extractSize(cell: Cell) => String = {
  val sheet = cell.getSheet()
  sheet
      .getRow(3)
      .getCell(cell.getColumn())
      .getStringValue()
}

val extractors = [extractName, extractSize]

```

## Here's one I prepared earlier

So that was the idea. This repo was my attempt at spiking that out to refine the idea
and see if there were any real problems. As such, it's spike+ quality rather than
production quality.

In the end the API looked something like this:


```scala
val workbook = WorkbookFactory.create(new File("examples/1/mps-figures.xls"))
val task = new Task("sheet1", "A2:A3",
  List(
    Extractor(

      keyFn = columnHeading("A1", "name"),  // since they're records, we need a key — this produces the key
                                            // here the function just checks the cell given contains the
                                            // second argument, and then returns the second argument
                                            // (this is to ensure the spreadsheet is still as the author
                                            //  envisaged)

      valueFn = column(0).andThen(getValue) // You can see it ended up more declarative — go to column 0,
                                            // then get the value.
                                            // You might also imagine cleaning up weird spreadsheets with:
                                            // column(0).andThen(coerceToString).andThen(value)
                                            // The idea is to create a library of 'operators' that can
                                            // be chained together to produce useful effects
    ),
    Extractor(
      keyFn = columnHeading("A2", "size"),
      valueFn = column(1).andThen(getValue)
    )
  )
)
task.exportWorkbookToRecords(workbook)
```

I originally planned to create a fully-fledged DSL for this, but after a week or so messing around with
parsers I decided it was more trouble than it was worth for this software no one had actually asked
for!