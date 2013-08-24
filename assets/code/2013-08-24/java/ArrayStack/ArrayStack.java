import java.util.Arrays;

public class ArrayStack<T> implements Stack<T> {
    private static final int INCREASE_SIZE = 8;
    private Object[] data;
    private int index;

    public ArrayStack() {
        data = new Object[0];
        index = -1;
    }

    @Override
    public boolean isEmpty() {
        return index == -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T pop() {
        return (T) data[index--];
    }

    @Override
    public void push(T value) {
        ensureCapacity();
        data[++index] = value;
    }

    private void ensureCapacity() {
        if (index == data.length - 1) {
            data = Arrays.copyOf(data, data.length + INCREASE_SIZE);
        }
    }


}
