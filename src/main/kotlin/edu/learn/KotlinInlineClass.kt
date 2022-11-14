package edu.learn

/**
 * @author VHBin
 * @date 2022/11/13-22:42
 */
class KotlinInlineClass

/**
 * inline class 内联类
 *
 * 业务逻辑有时需要为某些类型创建包装类, 创建包装类就需要有额外的内存开销, 且包装的类型为原始类型, 将极大的影响性能
 * (原始类型底层有大量优化, 但是使用包装类会将优化移除, 而包装类又没有优化处理)
 *
 * Kotlin 引入 inline class 处理
 * inline class 是 value-based 类的子集(没有身份, 只有 value)
 *
 * 使用 value 定义一个内联类, 同时需要 @JvmInline 注解修饰(使用 inline 修饰已被弃用)
 */
@JvmInline
value class Password(private val string: String)

/**
 * 内联类必须在主构造器中初始化单个属性.
 * 使用时, 实际上并不会实例化内联类
 * 运行时, 对应的变量只会包含内联类中定义的变量
 */
val securePassword = Password("Test For Inline Class")

/**
 * 内联类的主要特性(使用时不实例化, 运行时只包含定义的单个变量) -> 类的数据内联到类的用法当中.
 */

/**
 * 内联类支持普通类的一些功能:
 * 1. 允许声明属性
 * 2. 运行声明函数
 * 3. 允许有 init 代码块
 * 但不能有 backing field, 只能有简单的的可计算属性(没有 lateinit 修饰/不能委派的属性)
 */
@JvmInline
value class Name(val s: String) {
    init {
        require(s.length > 0) { }
    }

    val length: Int
        get() = s.length

    fun greet() {
        println("Hello, $s")
    }
}

fun main() {
    val name = Name("Kotlin")
    name.greet() // method `greet` is called as a static method
    println(name.length) // property getter is called as a static method
}