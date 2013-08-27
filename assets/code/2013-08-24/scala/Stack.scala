trait Stack[+A] {
  def push[B >: A](elem: B): Stack[B]

  def pop(): Stack[A]

  def top(): A

  def isEmpty(): Boolean

  def foreach[B](f: A => B) {
    if (!this.isEmpty) {
      f(this.top)
      this.pop.foreach(f)
    }
  }
}

object Stack {
  def apply[T](values: T*): Stack[T] = {
    values match {
      case Nil => EmptyStack
      case _ => values.foldLeft[Stack[T]](EmptyStack)(_ push _)
    }
  }
}

object EmptyStack extends Stack[Nothing] {
  def push[A](elem: A): Stack[A] = NonEmptyStack[A](elem, this)

  def isEmpty(): Boolean = true

  def pop(): Nothing = sys.error("Empty stack can not pop!")

  def top(): Nothing = sys.error("Empty stack can not top!")
}

case class NonEmptyStack[A](head: A, tail: Stack[A]) extends Stack[A] {
  def push[B >: A](elem: B): Stack[B] = NonEmptyStack(elem, this)

  def pop(): Stack[A] = tail

  def top(): A = head

  def isEmpty(): Boolean = false
}

object Main extends App{
    Stack(1, 2l, 3.0f, 4.0, "test", null).foreach(println)
}
