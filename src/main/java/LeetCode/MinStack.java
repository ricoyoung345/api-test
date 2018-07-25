package LeetCode;

import java.util.Stack;

class MinStack {
	private Stack<Integer> normalStack;
	private Stack<Integer> minStack;
	private int stackCurrentMin;

	/** initialize your data structure here. */
	public MinStack() {
		normalStack = new Stack<>();
		minStack = new Stack<>();
		stackCurrentMin = Integer.MAX_VALUE;
	}

	public void push(int x) {
		normalStack.push(x);
		if (x < stackCurrentMin) {
			minStack.push(x);
			stackCurrentMin = x;
		} else {
			minStack.push(stackCurrentMin);
		}
	}

	public void pop() {
		if (!normalStack.isEmpty() && ! minStack.isEmpty()) {
			normalStack.pop();
			minStack.pop();
			if (!minStack.isEmpty() && minStack.peek() > stackCurrentMin) {
				stackCurrentMin = minStack.peek();
			} else if (minStack.isEmpty()) {
				stackCurrentMin = Integer.MAX_VALUE;
			}
		}
	}

	public int top() {
		if (!normalStack.isEmpty()) {
			return normalStack.peek();
		}

		return 0;
	}

	public int getMin() {
		return stackCurrentMin;
	}
}