---
layout: post
title: "Java与Scala 中的不变、协变与逆变"
description: ""
category: "Scala"
tags: ["Java","类型","Scala"]
---
{% include JB/setup %}


我们知道，Java是一门静态强类型语言，Scala作为可以与Java高度兼容的新型语言，也拥有这个性质。下面，我们来看一下Java和Scala中，在处理类型继承结构中的泛型（Scala中称为类型参数）时都有哪些异同。

##引子 - 从null到Option

首先，从null说起，在Java中，Object是所有类型的父类，而表达式：

	null instanceof Object

的值却为false，也就是说，null不是对象，也没有类型的概念。但是，对于任意返回类型的函数，Java编译器并不阻止函数返回null，这就导致了某种程度上的不一致。为什么会存在这样的不一致呢？原因是在Java中，null关键字解决了如何表达“没有可供返回的对象”这个语义的问题，但也导致了两个新问题：第一，对于值类型（即原生类型），如何表达“没有可供返回的值”？第二，由于NullPointerException属于RuntimeException，当函数有可能返回null时，除了阅读源码或者文档，用户并不能从函数签名中得到一个明确的提示。第一个问题可以通过自动装箱功能得到解决，代价是牺牲一些性能；对于第二个问题，一些第三方库，如Guava，提供了一个“Optional”类，用来表达“可能没有可用的对象返回”。

那么，Scala对于这个问题有什么改进呢？

在Scala中，null得到了保留，但是null拥有类型，在REPL中，我们可以看到：

	scala> null
	res0: Null = null

可见null的类型是Null（null也是Null类型唯一的实例），但是，null的行为与Java一致，可以作为任何返回类型是AnyRef及其子类的函数的返回值，表达式

	null.isInstanceOf[AnyRef]

的值也仍然是false，从这一点上说，Scala中的null更加让人迷惑，对此我的理解是Scala中保留null只是为了和Java兼容，对于需要返回null的场景，Scala提供了更好的方案，就是类似于上面提到过的Guava库中Optional的类型，名字叫做Option。

##不变与协变

那么，Guava库中的Optional类和Scala中的Option类用法是否完全相同呢？让我们做个实验。

###不变
假如有一个函数，返回类型是Object，但是也有可能返回null，在Java中，利用Guava库，我们可以这样写：

	Optional<Object> method() {
		return Optional.of(new Object());
	}

Optional是泛型化的类，如果尝试返回一个Optional<String>会怎么样？

	Optional<Object> method() {
		return Optional.of("test");
	}
	
这样会导致编译失败，为什么呢？阅读Optional.of的源码：

	public static <T> Optional<T> of(T reference) {
		return new Present<T>(checkNotNull(reference));
	}

可以看到，of方法也是泛型化的方法，如果我们传一个String给它，那么它就会返回Present<String>，而在Java中，虽然String是Object的子类，但是Present<String>却不是Present<Object>的子类。

为什么Java要设计成这样？

这个问题涉及到了一个称之为“协变（Covariance）”的概念，**所谓协变，指的是对于带有类型参数的类Class[A]，当类Sub是类Base的子类时，如果类Class[Sub]是类Class[Base]的子类，那么我们说Class[A]是协变的。**

Java不支持协变，事实上，Present<String>不是Present<Object>的子类，Present<Object>也不是Present<String>的子类，这种性质，称为不变。

我们继续研究这个问题，首先看一下，如果引入协变，会发生什么。

在Java中，数组与泛型容器不同，拥有类似协变的性质（我认为这是Java的另一个不一致性），我们可以利用这一特性做以下实验，考虑代码：

	String[] a = new String[]{"a", "b", "c"};
	Object[] b = a;
	b[0] = 1;
	
上述代码可以通过编译，却不能运行，会抛出ArrayStoreException异常。这个例子解释了为什么泛型化容器不支持协变：强化类型安全。

###协变
在这一方面，Scala就更加灵活方便了，在Scala中，Option是协变的，代码：

	def method:Option[Any] = Some("abc")
	
可以通过编译，运行时也不会出错。

阅读Some的源码：

	final case class Some[+A](x: A) extends Option[A] {
  		def isEmpty = false
  		def get = x
	}
	
可以发现相对于Java的泛型，Scala中的类型参数前增加了一个+号，在Scala中，这里的+号表示支持协变，如果不加，则表示参数类型是不变的，即与Java中的表现相同。

那么，什么时候应该使用+号，什么时候不能使用+号呢？结合上面举的例子，可以根据以下规则判断协变的应用场景：

	如果使用类型参数，将类T包装到类C中（很多情况下，这表示C的实例中会存在一个指向T类型实例的引用），并且在使用C的实例时，只需要读取T实例的值，而不需要修改T实例的引用，那么就可以将C的类型参数设置成协变。

由于Some中不包含修改x的操作，所以设计成协变的可以更加方便使用。

##逆变

相对应与协变，在Scala中，还有一个概念，称为“逆变（Contravariance）”，使用-号表示，逆变与协变相反，**对于带有类型参数的类Class[A]，当类Sub是类Base的子类时，类Class[Base]是类Class[Sub]的子类，那么我们说Class[A]是协变的。**

逆变的适用场景也是与协变相反的：

	如果使用类型参数，将类T包装到类C中，并且在使用C的实例时，只需要写入T实例的值，而不需要读取，那么就可以将C的类型参数设置成逆变。

哪种场景下，我们只需要写入不需要读取呢？在Java中，似乎不存在这样的场景，而在Scala中，因为函数成为了第一等类型，对于一个函数类型的值，可以这样理解：用户会提供（写入）参数给函数，并从函数那里得到（读取）返回值，这时，对于用户来说，参数就是只会写入不需要读取的。在Scala中，函数也是使用对象实现的，简单起见，以只有一个参数的函数作为示例，其类型是这样的：

	trait Function1[-T1, +R]
	
T1是参数类型，R是返回值类型，因此，对于类型Function1[T1, R]来说，所有参数类型是T1或者T1的父类，并且返回值类型是R或者R的子类的函数，都是Function1[T1, R]的实例。

##结语
通过上面的分析对比，我们可以发现，相对于Java中的泛型仅仅支持不变的做法，Scala提供了更多的选择，为用户带来了更强的表达力，更简洁的语法。
