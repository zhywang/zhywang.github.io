public enum StackFactory {

    FPSTACKFACTORY {
        @Override
        public <T> Stack<T> createStack() {
            return new EmpytStack<T>();
        }

        @Override
        public <T> Stack<T> createStack(T... values) {
            return createStack(values, 0, new EmpytStack<T>());
        }

        @Override
        public <T> Stack<T> createStack(T[] values, int current, Stack<T> stack) {
            if (current == values.length)
                return stack;
            Stack<T> newStack = stack.push(values[current]);
            current += 1;
            return createStack(values, current, newStack);
        }
    };

    public abstract <T> Stack<T> createStack();

    public abstract <T> Stack<T> createStack(T... values);

    public abstract <T> Stack<T> createStack(T[] values, int current, Stack<T> stack);

}

class EmpytStack<T> extends Stack<T> {
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
