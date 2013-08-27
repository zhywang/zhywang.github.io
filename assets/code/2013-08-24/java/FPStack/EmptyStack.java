public class EmptyStack<T> extends Stack<T> {
    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public T top() {
        throw new IllegalArgumentException("Empty stack has no top elem!");
    }

    @Override
    public Stack<T> pop() {
        throw new IllegalArgumentException("Empty stack can not pop!");
    }

    @Override
    public Stack<T> push(T elem) {
        return new NonEmptyStack<T>(elem, this);
    }

}

class NonEmptyStack<T> extends Stack<T> {
    private final T head;
    private final Stack<T> tail;

    NonEmptyStack(T head, Stack<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public T top() {
        return head;
    }

    @Override
    public Stack<T> pop() {
        return tail;
    }

    @Override
    public Stack<T> push(T elem) {
        return new NonEmptyStack<T>(elem, this);
    }
}
