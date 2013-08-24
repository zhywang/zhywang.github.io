public class Main {
    public static void main(String[] args) {
        Stack<Integer> stack = StackFactory.FPSTACKFACTORY.createStack(0, 1, 2, 3, 4, 5);
        while (!stack.isEmpty()) {
            System.out.println(stack.top());
            stack = stack.pop();
        }
    }
}
