public class EmpytStack<T> extends Stack<T> {
    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public T top() {
        throw new IllegalArgumentException("Empyt stack has no top elem!");
    }

    @Override
    public Stack<T> pop() {
        throw new IllegalArgumentException("Empyt stack can not pop!");
    }

    @Override
    public Stack<T> push(T value) {
        return new NonEmptyStack<T>(value, this);
    }

}

class NonEmptyStack<T> extends Stack<T> {
    private final T value;
    private final Stack<T> oldStack;

    NonEmptyStack(T value, Stack<T> oldStack) {
        this.value = value;
        this.oldStack = oldStack;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public T top() {
        return value;
    }

    @Override
    public Stack<T> pop() {
        return oldStack;
    }

    @Override
    public Stack<T> push(T value) {
        return new NonEmptyStack<T>(value, this);
    }
}
