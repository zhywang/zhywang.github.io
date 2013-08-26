---
layout: post
title: "关于命令式编程、面向对象编程与函数式编程的思考"
description: ""
category: "Scala"
tags: ["Java","函数式编程","Scala"]
---
{% include JB/setup %}


继命令式编程与面向对象编程之后，函数式编程成为近来比较热门的编程范式之一，那么，命令式编程、面向对象编程与日渐流行的函数式编程，三种编程范式背后的思想有什么区别？本文就是我对这个问题的一些思考，分享出来，与大家探讨。

在下文中，我会通过实现一个简单的数据结构－－栈（Stack），来演示不同编程范式在解决具体问题时的实践。

为了简化起见，本文中实现的Stack只需要提供以下3个函数：

* is_empty： 返回一个标志，表示当前栈是否为空
* pop： 从栈顶弹出一个元素，若栈为空，则出错
* push： 将一个元素压入栈

pop和push这两个函数至少应该支持int型数据的出入栈。

### 语言选择

* 命令式编程：经典的C语言
* 面向对象编程：流行的Java语言
* 函数式编程：近年来新兴的基于JVM平台的Scala语言


### 不同编程范式中的实现

#### 命令式

作为命令式编程语言代表的C语言，具有语法简洁，功能强大，支持结构化编程的特点，也是各计算机专业的必修课。

在C语言中实现Stack，简单的解决方案是使用数组存储数据，代码如下：

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

上面这段代码非常简单，但是通过这段代码我们仍然不难发现C语言的不便之处：

* 不支持数据与操作绑定：由于难以实现面向对象类编程语言中的this关键字的功能，导致数据和操作分离。
* 模块化的支持差：在stack.h文件中，声明接口函数的同时，也定义了具体实现使用的数据结构，造成声明和实现的紧耦合，如果修改实现（比如使用链表代替数组），则作为接口的stack.h文件也要同时修改。
* 不支持数据隔离：对于任意代码，Stack中的数据都是可见的。

下面我们看一下面向对象编程语言中的做法。


#### 面向对象式

Java语言是目前最流行的面向对象式编程语言，相比C++，Java的语法更加简单，而且支持自动内存管理，提高开发效率。

使用Java实现Stack时，我们仍然可以在底层使用数组来保存数据，代码如下：


###### ~/java $ cat Stack.java

	public interface Stack<T> {
	    boolean isEmpty();
	    T pop();
	    void push(T value);
	}

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

在这段代码中，利用面向对象编程语言的封装、继承及多态的特性，解决了上一节的几个问题：

*  &nbsp;数据与操作封装成类，使用时更加自然直观。
*  &nbsp;接口Stack只声明操作，不需要定义具体实现，不同的实现可以有不同的内部数据结构。
*  &nbsp;实现了数据隔离，无法从外部访问具体实现内部的数据结构。



#### 换一种思考方式

对于示例，我们可以用另外一种思路来实现需求：

* is_empty： 只有当Stack为空时，才会返回true，否则为false。
* pop： 将原来的Stack拆成两部分，第一部分是栈顶的元素，第二部分是除去第一部分之后的Stack，返回第一部分。
* push： 使用新的元素和原来的Stack，拼成一个新Stack，这个Stack由两部分组成，第一部分是新元素，第二部分是原来的栈。

在这种实现中，pop后原来的栈内容不会改变，为了解决这个问题，我们可以增加一个方法，并修改pop的行为：

* top： 返回栈顶元素。
* pop： 将原来的Stack拆成两部分，第一部分是栈顶的元素，第二部分是除去第一部分之后的Stack，返回第二部分。

代码如下：

首先仍然是定义接口：
		
	public abstract class Stack<T> {
	    public abstract boolean isEmpty();
	
	    public abstract T top();
	
	    public abstract Stack<T> pop();
	
	    public abstract Stack<T> push(T value);
	}

然后是实现：

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

使用方法：

	public class Main {
	    public static <T> Stack<T> createStack(T... values) {
	        return createStack(values, 0, new EmpytStack<T>());
	    }
	
	    public static <T> Stack<T> createStack(T[] values, int current, Stack<T> stack) {
	        if (current == values.length)
	            return stack;
	        Stack<T> newStack = stack.push(values[current]);
	        current += 1;
	        return createStack(values, current, newStack);
	    }
	    private static <T> void printStack(Stack<T> stack) {
	        if (stack.isEmpty()) 
	            return;
	        else {
	            System.out.println(stack.top());
	            printStack(stack.pop());
	        }
	    }
	    public static void main(String[] args) {
	        printStack(createStack(0, 1, 2, 3, 4, 5));
	    }
	}
	
相比上面的实现，使用这种方式的代码更加容易理解，并且有下面两个特点：

* 内部状态不可改变：Stack的属性全部是final的，每次压栈都会创建新的Stack，在Java中导致程序运行时创建大量对象。
* 使用递归而不是循环：初始化和遍历栈时，都使用递归而不是循环，由于Java缺少对于递归的优化，运行时资源消耗会增加。

这两个特点也是函数式编程的特点，

下面，我们看一下在原生支持函数式编程的Scala中，我们可以怎么做。

#### 函数式

由于项目的需要，最近我在学习使用Scala语言，相比Java语言，Scala语言原生支持函数式编程，并且基于JVM，对于有Java语言经验的程序员来说比较容易学习。下面是我沿用上一小节的思路，使用Scala语言实现的Stack：


	trait Stack[+A] {
	  def push[B >: A](elem: B): Stack[B]
	
	  def pop(): Stack[A]
	
	  def top(): A
	
	  def isEmpty(): Boolean
	}
	

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
	  def createStack[T](values: T*): Stack[T] = {
	    values match {
	      case Nil => EmptyStack
	      case _ => values.foldLeft[Stack[T]](EmptyStack)(_ push _)
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

可以看到，相比上一节中的Java实现，这段代码有如下特点：

* 语法更加简洁。

* 提供更多的函数支持遍历和递归。
