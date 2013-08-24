public class Main {
    public static void main(String[] args) {
        Stack<Integer> stack = new ArrayStack<Integer>();
        for (int i = 0; i < 10; i++)
            stack.push(i);
        while (!stack.isEmpty())
            System.out.println(stack.pop());
    }
}
