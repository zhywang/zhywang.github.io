---
layout: post
title: "Java和Scala中泛型的比较"
description: ""
category: "Scala"
tags: ["Java","Scala", "泛型"]
---
{% include JB/setup %}


泛型是Java自5.0版本起加入的新特性，通过泛型可以增强Java语言类型系统功能，实现编译期间的类型检查，提高类型安全，同时也减少了很多强制类型转换工作。

但是Java的泛型系统并不完善，由于泛型只在编译期间得到支持，编译后的代码，泛型参数会被抹除，导致Java中的泛型受到以下限制（[官方文档](http://docs.oracle.com/javase/tutorial/java/generics/restrictions.html)）：

* 原生类型无法作为类型参数传递：Java中存在原生类型，泛型只支持对象类型，不支持原生类型。
* 无法直接实例化类型参数。
* 类型参数无法作为静态方法的返回类型。
* 无法将对象强制转换成参数化类型，也无法对参数化类型进行instanceof操作。
* 无法创建参数化类型的数组。
* 无法创建、捕获或者抛出参数化类型的异常。
* 类型参数不会成为方法签名的成分，因此无法重载。

