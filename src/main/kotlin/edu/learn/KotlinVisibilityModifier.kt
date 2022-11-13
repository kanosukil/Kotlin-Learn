package edu.learn

/**
 * @author VHBin
 * @date 2022/11/12-22:06
 */
/**
 * 可见性修饰符:
 * 类, 对象, 接口, 构造器, 函数, 属性以及其 setter 都可以设置可见性修饰符
 * (getter 与其对应的属性修饰符一致)
 * 类 Java
 * Kotlin 修饰符:
 * 1. public (default)
 * 2. internal
 * 3. protected
 * 4. private
 */

/**
 * 类, 接口, 对象, 函数, 属性可以在包的顶层直接声明可见性
 * 默认可见性: public
 * private 只有在当前文件中可见
 * internal 同 module 下可见
 * protected 不支持顶层声明
 * 使用其他包的顶层声明需要使用 import 引入
 */
class KotlinVisibilityModifier

private fun kotlinVisibilityModifierFunction() {

}

var bar: Int = 5 // property is visible everywhere
    private set         // setter is visible only in example.kt

internal const val baz = 6    // visible inside the same module

/**
 * 类成员可见性
 * 1. private 只有该类的所有成员可见
 * 2. protected 子类可见
 * 3. internal 同 module 可见
 * 4. public 全部可见
 * 注: kotlin 的外部类不可见内部类的私有成员(与 Java 区分)
 */

open class Outer {
    private val a = 1
    protected open val b = 2
    internal open val c = 3
    val d = 4  // public by default

    protected class Nested {
        val e: Int = 5
    }
}

class Subclass : Outer() {
    // a is not visible
    // b, c and d are visible
    // Nested and e are visible

    override val b = 5   // 'b' is protected
    override val c = 7   // 'c' is internal
}

class Unrelated(o: Outer) {
    // o.a, o.b are not visible
    // o.c and o.d are visible (same module)
    // Outer.Nested is not visible, and Nested::e is not visible either
}

/**
 * 构造器
 * 指定构造器的可见性需要指明 constructor 关键字
 * 对于主构造器: 在类头指明可见性
 * 默认情况: public(任何地方可见)
 */

internal open class Test {
    private var id: Int

    internal constructor(id: Int) {
        this.id = id
    }

    protected constructor() : this(1)

    private constructor(name: String) : this(name.length)
}

/**
 * 本地变量不支持使用可见性
 */

/**
 * Module 模块: 一个 module 可以是
 * 1. IDEA module
 * 2. Maven project
 * 3. gradle source set(除 test 源集可以访问 main 的内部声明外)
 * 4. 一次调用 <kotlinc> Ant task 编译的一组文件
 */