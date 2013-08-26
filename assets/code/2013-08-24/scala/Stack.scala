trait Stack[+A] {
  def push[B >: A](elem: B): Stack[B]

  def pop(): Stack[A]

  def top(): A

  def isEmpty(): Boolean
}

object EmptyStack extends Stack[Nothing] {
  def push[A](elem: A): Stack[A] = NonEmptyStack[A](elem, this)

  def isEmpty(): Boolean = true

  def pop(): Nothing = sys.error("Empty stack can not pop!")

  def top(): Nothing = sys.error("Empty stack can not top!")
}

case class NonEmptyStack[A](elem: A, tail: Stack[A]) extends Stack[A] {
  def push[B >: A](elem: B): Stack[B] = NonEmptyStack(elem, this)

  def pop(): Stack[A] = tail

  def top(): A = elem

  def isEmpty(): Boolean = false
}

object Main {
  def createStack[T](values: T*): Stack[T] = {
    values match {
      case Nil => EmptyStack
      case _ => values.foldLeft[Stack[T]](EmptyStack)(_ push _)
    }
  }

  def printStack[A](s: Stack[A]): Unit = {
    s match {
      case EmptyStack => println
      case stack => {
        println(s.top())
        printStack(s.pop())
      }
    }
  }

  def main(args: Array[String]): Unit = {
    printStack(createStack(1, 2l, 3.0f, 4.0, "test", null))
  }
}
