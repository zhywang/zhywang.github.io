---
layout: post
title: "命令式编程、面向对象编程与函数式编程比较"
description: ""
category: "Scala"
tags: []
---
{% include JB/setup %}



继命令式编程与面向对象编程之后，函数式编程是近来比较热门的编程范式之一。

那么，从命令式编程，到面向对象编程，再到近来日渐流行的函数式编程，它们之间的区别是什么呢？编程范式的进化，又反映了业界发展过程中怎样的趋势？本文是作者关于这两个问题的一些初步的思考，并结合一个很简单的例子来进行解释。

### 需求
本文将会通过实现一个简单的数据结构－－栈（Stack），来演示不同编程范式在解决具体问题时的思想。

为了简化起见，本文中实现的Stack只需要提供以下3个功能：

* is_empty： 返回一个标志，表示当前栈是否为空
* pop： 从栈顶弹出一个元素，若栈为空，则出错
* push： 将一个元素压入栈

pop和push至少支持int型数据的出入栈。

### 语言选择

* 命令式：经典的C语言
* 面向对象：流行的Java语言
* 函数式：近年来新兴的基于JVM平台的Scala语言


### 不同编程范式中的实现

#### 命令式

在C语言中实现Stack，一个比较容易想到的解决方案是底层存储数据时使用数组，沿着这种思路，我们可以写出如下代码：

###### ~/c $ cat stack.h

	#ifndef _STACK_H
	#define _STACK_H
	
	typedef struct stack{
		int *data;
		int capacity;
		int index;
	} *Stack;
	
	Stack create();
	int is_empty(Stack);
	int pop(int*, Stack);
	int push(int, Stack);
	
	#endif
	
程序中定义了一个struct作为Stack的类型，并使用一个指向int数据的指针来保存Stack中数据所在的位置，使用两个变量来保存Stack当前的容量以及栈顶数据的index，声明了四个函数，来完成Stack的创建及操作。

这四个函数的定义如下：
	
###### ~/c $ cat stack.c
	
	#include "stack.h"
	#include <stdlib.h>
	
	#define INCREASE_SIZE 8
	int ensureCapacity(Stack stack) {
	    if(stack->index + 1 < stack->capacity)
	        return 1;
		int* temp= (int*)realloc(stack->data, sizeof(int)*(stack->capacity + INCREASE_SIZE));
	    if(temp == NULL){
	        return 0;
	    }
		stack->data=temp;
		stack->capacity+=INCREASE_SIZE;
	    return 1;
	}
	
	Stack create() {
		Stack stack=(Stack)malloc(sizeof(struct stack));
		if(stack) {
			stack->data=NULL;
			stack->index=-1;
			stack->capacity=0;
		}
		return stack;
	}
	
	int is_empty(Stack stack) {
	    return stack->index == -1;
	}
	
	int pop(int *elem, Stack stack) {
		if(is_empty(stack))
	        return 0;
		*elem = stack->data[stack->index];
		stack->index--;
		return 1;
	}
	
	int push(int value, Stack stack) {
		if(!ensureCapacity(stack))
			return 0;
		stack->data[++(stack->index)]=value;
		return 1;
	}

我们可以这样使用上面定义的栈：

###### ~/c $ cat main.c

	#include <stdio.h>
	#include "stack.h"
	int main(int argc, char* argv[]) {
		int i=0;
		Stack s=create();
		while(i<10 && push(i, s))
			i++;
		while(!is_empty(s) && pop(&i, s))
			printf("%d\n",i);
	}

通过上面的演示，我们不难发现C语言的不便之处：

* 对于程序模块化的支持比较差：在stack.h文件中，声明接口函数的同时，也定义了具体实现使用的数据结构，造成声明和实现的紧耦合，如果修改实现（比如使用链表代替数组），则作为接口的stack.h文件也要同时修改。
* 不支持数据与操作的绑定：由于难以实现面向对象类编程语言中的this关键字的功能，导致数据和操作分离。
* 不支持数据隔离：数据对于任意代码都是可见的。

#### 面向对象式

使用Java实现时，我们仍然可以在底层使用数组来保存数据，代码如下：

首先定义一个接口，声明Stack所支持的操作：

###### ~/java $ cat Stack.java

	public interface Stack<T> {
	    boolean isEmpty();
	    T pop();
	    void push(T value);
	}
	
然后是具体实现类，底层数据存储使用数组：

###### ~/java $ cat ArrayStack.java
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
	
我们可以这样使用以上定义的Stack：

###### ~/java $ cat Main.java

	public class Main {
	    public static void main(String[] args) {
	        Stack<Integer> stack = new ArrayStack<Integer>();
	        for (int i = 0; i < 10; i++)
	            stack.push(i);
	        while (!stack.isEmpty())
	            System.out.println(stack.pop());
	    }
	}

在这段代码中，利用面向对象范式的封装、继承及多态的特性，解决了上一节中面向过程范式的几个问题：

* 接口Stack只声明操作，不需要定义具体实现，不同的实现可以有不同的内部数据结构。
* 数据与操作绑定，使用时更加自然直观。
* 实现了数据隔离，无法从外部访问具体实现内部的数据结构。

同时这些特性也促进了面向对象编程范式的普及，使其成为当今最流行的编程范式。

#### 关于面向对象式的进一步思考

就本文示例的需求来说，进一步思考，我们可以用另外一种方式阐释需求：

* is_empty： 只有当Stack为空时，才会返回true，否则为false。
* pop： 将原来的Stack拆成两部分，第一部分是栈顶的元素，第二部分是除去第一部分之后的Stack。
* push： 使用新的元素和原来的Stack，拼成一个新Stack，这个Stack由两部分组成，第一部分是新元素，第二部分是原来的栈。

此时，pop将会有两个返回值，为了解决这个问题，我们可以增加一个方法，并修改pop的行为：

* top： 返回栈顶元素。
* pop： 将原来的Stack拆成两部分，第一部分是栈顶的元素，第二部分是除去第一部分之后的Stack，返回第二部分。

这种思路已经接近函数式编程了，沿着这个思路，我们得到以下代码：

首先仍然是定义接口：
	
	public abstract class Stack<T> {
	    public abstract boolean isEmpty();
	
	    public abstract T top();
	
	    public abstract Stack<T> pop();
	
	    public abstract Stack<T> push(T value);
	}

然后是实现：

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

使用方法：
	
	public class Main {
	    public static void main(String[] args) {
	        Stack<Integer> stack = StackFactory.FPSTACKFACTORY.createStack(0, 1, 2, 3, 4, 5);
	        while (!stack.isEmpty()) {
	            System.out.println(stack.top());
	            stack = stack.pop();
	        }
	    }
	}
	
使用这种方式实现的Stack有以下特点：

* Stack内部状态不可改变：内部状态全部是final的，每次压栈都会创建新的Stack。
* 尽量使用递归：初始化和遍历栈时，都使用递归而不是循环。
* 会创建大量对象，增加运行时资源消耗。

另外，由于Java对函数式编程支持不好，初始化时我们仍然使用了可变量（使用crruent遍历values）。

下面，我们看一下在支持函数式编程的Scala中，我们可以怎么做。

#### 函数式

仍然沿用上一小节的思路，给出Scala中的Stack实现：

定义接口：

	trait Stack[+A] {
	  def push[B >: A](elem: B): Stack[B]
	
	  def pop(): Stack[A]
	
	  def top(): A
	
	  def isEmpty(): Boolean
	}
	
实现：

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

使用：

	object Main {
	  def createStack(values: Any*): Stack[Any] = {
	    values match {
	      case Nil => EmptyStack
	      case _ => values.tail./:(EmptyStack.push(values.head))(_.push(_))
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
