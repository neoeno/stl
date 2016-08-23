package troglodyte.stl

case class Transformer[T](keyFn: T => String, valueFn: T => Any)
