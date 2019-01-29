package utils

object ClosableWrapper {
  def apply[T <: AutoCloseable, R](o: T)(action: T => R): R =
    new ClosableWrapper(o).run(action)
}

class ClosableWrapper[T <: AutoCloseable](o: T) {
  def run[R](action: T => R): R =
    try {
      action(o)
    } finally {
      o.close()
    }
}
