package troglodyte.stl

/* Extractor
 * A pair of functions to produce a key and a value, essentially
 * a map entry.
 *
 * Let's say we want to produce a map from names of numbers to
 * integers, like:
 *   "one" => 1
 *   "two" => 2
 *   ...etc
 *
 * And we have functions englishName(i: Int) and identity(i: Int)
 * that just returns the integer.
 *
 * We use a Extractor to store this pair of functions. In fact,
 * now I think of it — this might be better as a real class with
 * an execute method on it...
 */
case class Extractor[T](keyFn: T => String, valueFn: T => Any)
