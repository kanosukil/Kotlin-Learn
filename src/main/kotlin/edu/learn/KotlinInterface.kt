package edu.learn

/**
 * @author VHBin
 * @date 2022/11/12-20:07
 */
interface KotlinInterface {
    val property1: Int // 默认为 abstract
    val property2: String
        get() = "Interface Property2"

    fun firstMethod()
    fun secondMethod() {
        TODO("接口中的实现方法")
    }
}

/**
 * kotlin 接口可以包含抽象方法的声明, 也可以包含方法的实现
 * 和 抽象类 的区别: interface 不能存储状态
 * 两者都可以拥有属性, 但其属性必须是 private 私有的 或 有访问器的实现
 */

class KotlinInterfaceChild : KotlinInterface {
    override val property1: Int = 21
    override fun firstMethod() {
        println("This is Child Override First Method")
    }
}

/**
 * 接口可以继承接口 -> 实现 & 扩展父接口
 */

interface Named {
    val name: String
}

interface PersonInter : Named {
    val firstName: String
    val lastName: String

    override val name: String get() = "$firstName $lastName"
    // 可以将此实现也交由子类实现
}

data class Employee(
    // implementing 'name' is not required
    override val firstName: String,
    override val lastName: String,
    val student: Student

) : PersonInter {
    override val name: String get() = "$firstName $lastName"
    // 重写上上层接口的属性
}

/**
 * 接口成员方法重写冲突
 * 子类继承多个接口, 多个接口的声明方法重复
 * 此情况下, 即使接口中已有实现, 也需要子类提供自己的实现方法
 */

/**
 * SAM(Single Abstract Method) 函数式接口
 * 一个只有一个抽象方法的接口 = 函数式接口/SAM接口
 * 函数式接口可以有多个非抽象方法, 但是只能有一个抽象方法
 */
fun interface FunctionalInterface {
    fun invoke()
    fun method0() {
        TODO("函数式接口非抽象方法")
    }

    fun method1() {
        TODO("函数式接口非抽象方法")
    }
}

/**
 * 使用函数式接口时, 通过 SAM 转换使用 lambda 表达式简化代码, 提高可读性
 * 不需要创建时实现接口的具体类, 而是通过 lambda 表达式实现函数式接口
 * 通过 SAM 转换, Kotlin 自动根据签名匹配 lambda 表达式到抽象方法上
 */
// 创建函数式接口 -> 变量赋值 lambda 实现的接口 -> 调用变量继承接口的对应方法

/**
 * 带有构造方法的接口迁移到函数式接口上
 */
interface Printer {
    fun print()
}

// fun 函数名(参数[lambda 表达式]): 返回值类型 = 返回对象: 函数式接口的实现
fun Printer(block: () -> Unit): Printer = object : Printer {
    override fun print() = block()
}

/**
 * 上面的接口 + fun 等同于 下面的函数式接口
 */
fun interface PrinterFunctional {
    fun print()
}

/**
 * PrinterFunctional 的构造函数被隐式创建, 可以使用 ::PrinterFunctional 调用
 */

/**
 * type aliases 类型别名
 * 使用类型别名重写函数式接口
 */

// 函数式接口
fun interface IntPredicate {
    fun accept(i: Int): Boolean
}

// Creating an instance using lambda
val isEven = IntPredicate { it % 2 == 0 }

// 类型别名
typealias Predicate = (i: Int) -> Boolean // 输入 -> 输出

val isObb: Predicate = { it % 2 != 0 }

fun main() {
    println("Is 7 obb? - ${isObb(7)}")
    println("Is 7 even? - ${isEven.accept(7)}")
}

/**
 * 类型别名 与 函数式接口 的不同用途:
 * 类型别名: 使用别名引用现有类型, 不会创建新的类型
 * 函数式接口: 会创建新的类型, 实现和扩展其他接口
 * 需要添加扩展时, 可以使用函数式接口
 *
 * 类型别名: 只能有一个成员
 * 函数式接口: 只能有一个抽象成员, 但是可以有多个非抽象成员
 *
 * 函数式接口 比 类型别名 更加灵活, 提供的功能也更多, 但是语法与资源开销也更高(转换为特定的接口)
 *
 * 如何选择:
 * 1. API 需要接收一个特定参数和返回类型的函数 -> 简单函数类型 或 类型别名
 * 2. API 需要接收比函数更复杂的实例 -> 函数式接口
 */