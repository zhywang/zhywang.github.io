public abstract class Stack<T> {
    public abstract boolean isEmpty();

    public abstract T top();

    public abstract Stack<T> pop();

    public abstract Stack<T> push(T value);
}
