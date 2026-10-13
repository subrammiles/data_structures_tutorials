package stack.a_basics;

import common.Check;

/*
 * Stack = Last In, First Out (LIFO). Think of a pile of plates.
 * Operations push, pop, peek are all O(1).
 *
 * Two implementations:
 *   ArrayStack  - array + top index. Cache friendly; must resize when full.
 *   LinkedStack - push/pop at the head of a linked list. Never full; one node allocation per push.
 *
 * Plus MinStack: getMin() in O(1) by storing, next to each value, the minimum at that moment.
 *
 * Where stacks appear: function call stack, undo, browser back button, balanced brackets,
 * expression evaluation, DFS, monotonic stack problems.
 * In Java prefer ArrayDeque over the legacy java.util.Stack (which is synchronised and extends Vector).
 */
public class Stacks {

    static class ArrayStack {
        private int[] data = new int[4];
        private int top = -1;

        void push(int x) {
            if (top == data.length - 1) data = java.util.Arrays.copyOf(data, data.length * 2);
            data[++top] = x;
        }

        int pop() {
            if (isEmpty()) throw new IllegalStateException("Stack underflow");
            return data[top--];
        }

        int peek() {
            if (isEmpty()) throw new IllegalStateException("Stack is empty");
            return data[top];
        }

        boolean isEmpty() {
            return top == -1;
        }
    }

    static class LinkedStack {
        private static class Node {
            int value;
            Node next;

            Node(int value, Node next) {
                this.value = value;
                this.next = next;
            }
        }

        private Node top;

        void push(int x) {
            top = new Node(x, top);
        }

        int pop() {
            if (top == null) throw new IllegalStateException("Stack underflow");
            int v = top.value;
            top = top.next;
            return v;
        }

        boolean isEmpty() {
            return top == null;
        }
    }

    static class MinStack {
        private final ArrayStack values = new ArrayStack();
        private final ArrayStack mins = new ArrayStack();

        void push(int x) {
            values.push(x);
            mins.push(mins.isEmpty() ? x : Math.min(x, mins.peek()));
        }

        int pop() {
            mins.pop();
            return values.pop();
        }

        int getMin() {
            return mins.peek();
        }
    }

    /* Classic application: are the brackets balanced? */
    static boolean isBalanced(String s) {
        ArrayStack stack = new ArrayStack();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else if (c == ')' || c == ']' || c == '}') {
                if (stack.isEmpty()) return false;
                char open = (char) stack.pop();
                if ((c == ')' && open != '(') || (c == ']' && open != '[') || (c == '}' && open != '{')) return false;
            }
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        ArrayStack a = new ArrayStack();
        for (int i = 1; i <= 6; i++) a.push(i); // forces a resize
        Check.eq("array pop", a.pop(), 6);
        Check.eq("array peek", a.peek(), 5);

        LinkedStack l = new LinkedStack();
        l.push(1);
        l.push(2);
        Check.eq("linked pop", l.pop(), 2);
        Check.eq("linked pop", l.pop(), 1);
        Check.throwsException("underflow", l::pop);

        MinStack m = new MinStack();
        m.push(5);
        m.push(2);
        m.push(7);
        Check.eq("min", m.getMin(), 2);
        m.pop();
        m.pop();
        Check.eq("min after pops", m.getMin(), 5);

        Check.isTrue("balanced", isBalanced("{[()()]}"));
        Check.eq("unbalanced", isBalanced("([)]"), false);
        Check.eq("unclosed", isBalanced("(("), false);
    }
}
