package edu.learn

import java.util.function.BinaryOperator
import java.util.function.IntBinaryOperator

/**
 * @author VHBin
 * @date 2022/11/13-22:40
 */
class KotlinEnumClass

/**
 * 枚举类
 * 作用: 类型安全枚举
 * 每一个枚举用例都是一个对象
 * 枚举用例之间使用逗号分隔, 用例与成员方法之间使用分号分隔
 */
// 无初始化的枚举类
enum class Direction {
    NORTH, SOUTH, WEST, EAST
}

// 带有初始化的枚举类
enum class Color(val rgb: Int) {
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF)
}

/**
 * 枚举匿名类
 */
enum class ProtocolState {
    WAITING {
        override fun signal() = TALKING
    },

    TALKING {
        override fun signal() = WAITING
    };

    abstract fun signal(): ProtocolState
}

/**
 * 在枚举类中实现接口
 * 默认情况下, 所有的枚举类都实现了 Comparable 接口, 且枚举类中的实例迭代顺序即为实例的声明顺序.
 */

enum class IntArithmetics : BinaryOperator<Int>, IntBinaryOperator {
    PLUS {
        override fun apply(t: Int, u: Int): Int = t + u
    },
    TIMES {
        override fun apply(t: Int, u: Int): Int = t * u
    };

    override fun applyAsInt(t: Int, u: Int) = apply(t, u)
}

fun enumClassInterface() {
    val a = 13
    val b = 31
    for (f in IntArithmetics.values()) {
        println("$f($a, $b) = ${f.apply(a, b)}")
    }
}

/**
 * 枚举类实例的使用
 * 所有的枚举类都有两个方法:
 * 1. EnumClass.valueOf(value: String): EnumClass 获取指定实例
 * 2. EnumClass.values(): Array<EnumClass> 获取实例列表
 * valueOf() 在 value 与枚举类中的任何实例都不匹配时抛出 IllegalArgumentException
 *
 * 可以通过 enumValues<T>() 和 enumValueOf<T>(name: String) 函数以泛型的方式访问枚举类中的常量
 */

enum class RGB { RED, GREEN, BLUE }

inline fun <reified T : Enum<T>> printAllValues() {
    print(enumValues<T>().joinToString { it.name })
}

/**
 * 每个枚举类实例都有的属性:
 * 1. val name: String
 * 2. val ordinal: Int (位置, 从 0 开始)
 */

fun main() {
    enumClassInterface()
    printAllValues<RGB>() // RED, GREEN, BLUE
}