package edu.learn

import java.io.File
import java.util.*

/**
 * @author VHBin
 * @date 2022/11/12-13:02
 */
class KotlinClassProperty {
    companion object KK {
        fun useProperty() {
            val stu = Stu("VHBin", 21)
            println("Student:\n${stu.info}")
            println("ID: ${stu.id}")
            stu.id = this.hashCode()
            println("ID: ${stu.id}")
            println(stu.numberVisibility)
        }
    }
}

fun main() {
    KotlinClassProperty.useProperty()
}


class Stu(var name: String, var age: Int) {
    /**
     * var 可变
     * val 只读
     */
    /**
     * 定义成员属性的完整语法:
     * var <propertyName> [: <PropertyType][= <Property_initializer>]
     *      [<getter>]
     *      [<setter>]
     * val <propertyName> [: <PropertyType][= <Property_initializer>]
     *      [<getter>]
     * 可以通过 Property_initializer 推断 PropertyType 的情况下, PropertyType 可省略
     * 自定义了 getter, 就会在每次调用该属性时运行该方法
     * 自定义了 setter, 就会在(除了初始化)设定该属性时运行该方法
     */
    val info get() = "Name: $name, Age: $age"
    var id = this.hashCode()
        get() = field.hashCode()
        set(value) {
            field = value.hashCode()
            numberVisibility = "$age-$field"
        }
    var numberVisibility = "$age-$id"
        private set // 私有 setter 且使用默认的实现方式

    /**
     * property Backing fields
     * field 访问器中引用, 标识对应的属性字段值
     * 若在 setter 中直接使用字段名赋值, 将产生因递归产生的 StackOverFlow 错误
     */
    // Backing Properties
    private var _table: Map<String, Int>? = null
    val table: Map<String, Int>
        get() {
            if (_table == null) {
                _table = HashMap() // Type parameters are inferred
            }
            return _table ?: throw AssertionError("其他线程重设为空")
        }

    fun method() {
        println(this)
    }
}

/**
 * 编译时已知 val 变量, 编译后将被 const 标记(编译时常量)
 * 此类变量满足:
 * 1. 顶层属性(全局变量) / 对象 或 伴生对象(companion object) 的成员
 * 2. 必须是 String 或 基础类型变量
 * 3. 不能有自定义 getter
 * 使用时, 编译器将 inline 内联该常量用法, 使用实际值替换该变量的引用.
 * 但是 field 并不会被删除 -> 可以使用 反射 reflection 来交互
 * 扩: 此类变量可用于注解
 */
const val SUBSYSTEM_DEPRECATED: String = "This subsystem is deprecated"

@Deprecated(SUBSYSTEM_DEPRECATED)
fun foo() {
    TODO("注解演示")
}

/**
 * 懒加载属性/变量
 * 一般情况下: 非空属性必须在构造器中进行初始化
 * 对于特殊情况(依赖注入, 单元测试设置方法的初始化)不方便, 不能在构造器中进行非空属性的初始化
 * 因此使用懒加载避免 null 检测
 * 使用 lateinit 关键字标注
 */
class MyTest {
    // 使用 lateinit 关键字标注 var 变量, 支持懒加载
    // 对于 var 变量只能在类中使用, 不能将 lateinit 变量移到主构造方法中且不能自定义 getter & setter
    // 支持顶层属性以及本地变量
    // 使用 lateinit 关键字还必须保证类别非空, 且不是基本数据类型
    // lateinit 变量在初始化前调用将返回一个特殊异常(清楚地说明对象没有初始化)
    lateinit var subject: Stu

    fun setup() {
        subject = Stu("VHBin", 21)
    }

    fun test() {
        subject.method()  // dereference directly
    }

    // lateinit 变量的初始化检测: 类::成员.isInitialized
    // 只适用于同一 class 中的属性 和 在同一文件的一个外部类且在文件的顶层声明时可以访问的属性
    fun subjectIsInitialized() = ::subject.isInitialized
}


fun checkIsInitial(obj: MyTest) = if (obj.subjectIsInitialized()) {
    println(obj.subject)
    true
} else {
    println("Subject has not initialized")
    false
}

/**
 * 属性的调用
 * 一般情况属性: 简单的读取与写入一个属性的 field
 * 支持自定义 getter & setter 的属性: 自定义读写逻辑访问属性的 field
 * 属性的共同行为模式: lazy value 延迟值, map 映射, 访问数据库, 访问监听器
 */

/**
 * 泛型: 使用类 Java
 */

class Box<T>(t: T) {
    var value = t
}

val intBox: Box<Int> = Box<Int>(21)

// 可以省略类型, 编译器通过传入构造器的值推断
val inferBox = Box(21)

/**
 * (Java 类型系统最棘手的方面之一) Kotlin 没有通配符系统, 作为替代, Kotlin 使用 声明-占位 分歧 和 类型预测
 * (Java 使用有界通配符来增加 API 的灵活性)
 * (泛型在 Java 中是不变的 <- List<String> 不是 List<Object> 的子类)
 * (泛型可变可能会发生编译通过, 运行时却发生异常 <- List<String> 的变量赋值给 List<Object>, 然后接受一个非 String 类型的值)
 * (Java 泛型不变可以确保运行时的安全性, 但是同时也有一定影响 <- Collection<E>.addAll(Collection<? extend E> items) 而不是 Collection<E>.addAll(Collection<E> items), 可以加入 E 的子类对象)
 * 逆变性(Java 中 List<? super String> 是 List<Object> 的父类 <- List<? super String> 写入的是 String 及其父类, 读出的是 Object)
 *
 * 只向其中写入的称为消费者( 消费者 向上延申) ? extend T -> 写入 T 及其子类
 * 只向其中读出的称为生产者( 生产者 向下扩展) ? super T -> 读出 T 及其父类
 *
 * 消费者和生产者都可以读写, 只是读写IO的对象类别不同. 通配符仅仅只是保证类型安全.
 */

/**
 * Kotlin: Declaration-Site Variance
 * 使用 in & out 关键字简化通配符问题
 * in & out 称为: variance annotation(为类型声明提供占位, 提供了声明-占位 variance)
 * out 表示可能只出不进, 返回时泛型位可以安全的转换成对应子类(输出 A(Base), 可以安全的转换为 A(Derived))
 *  可以说是 A<out T>, A 是 T 的生产者, 可以生产基于 T 的东西(即子类)
 * in 表示可能只进不出, 输入时泛型位可以安全的转换为对应的子类,
 *
 * in & out 可以类比于 Java 的 泛型的 extend & super
 */

interface Source<out T> {
    fun nextT(): T
}

interface Comparable<in T> {
    operator fun compareTo(other: T): Int
}

fun demo(ss: Source<String>, x: Comparable<Number>) {
    val objects: Source<Any> = ss // This is OK, since T is an out-parameter
    // ...
    x.compareTo(1.0) // 1.0 has type Double, which is a subtype of Number
    // Thus, you can assign x to a variable of type Comparable<Double>
    // 因此, 你可以将 x 赋值给 Comparable<Double> 类型的变量
    val y: Comparable<Double> = x // OK!

    // out 可以将泛型位子类赋值给泛型位父类(前提: 只出不进) 协变 extend
    // in 可以将泛型位父类赋值给泛型位子类(前提: 只进不出) 逆变 super
}

/**
 * 类型预测 Type Projections
 * 设置 out 限制子类型输入, 设置 in 限制父类型输出.
 * 但是一般不可能只有单向数据流动
 */

class Array<T>(val size: Int) {
    operator fun get(index: Int): T {
        TODO("Array Get")
    }

    operator fun set(index: Int, value: T) {
        TODO("Array Set")
    }

}

// from 出
fun copy(from: Array<out Any>, to: Array<Any>) {
    TODO("Array Copy")
}

// dest 入
fun fill(dest: Array<in String>, value: String) {
    TODO("Array Fill")
}

/**
 * Star-projections
 * 泛型投影, 具体实例化的都是该投影泛型的子类型
 * 1. Foo<out T: TUpper> TUpper 为 T 的上限值, 使 Foo<*> = Foo<out TUpper> -> T 未知时, 可以从 Foo<*> 中安全的读取 TUpper 值
 * 2. Foo<in T> Nothing 为 T 的下限值, 使 Foo<*> = Foo<in Nothing> -> T 未知时, 不能安全的向 Foo<*> 中写入值
 * 3. Foo<T: TUpper> T 为不变值, 上限为 TUpper -> T 未知时的 Foo<*>, 读取等同于 Foo<out TUpper>, 写入等同于 Foo<in Nothing>
 *
 * 多个泛型类型参数, 每个类型参数都可以独立投影.
 */

interface Function<in T, out U>
/**
 * 1. Function<*, String> -> Function<in Nothing, String>
 * 2. Function<Int, *>    -> Function<Int, out Any?>
 * 3. Function<*, *>      -> Function<in Nothing, out Any?>
 */

/**
 * 泛型函数
 * fun <T> 函数名(参数名: T 或其他类型, ...) : T 或其他类型呢 {
 *      函数体
 * }
 * 使用时, 若参数中使用了泛型, 即可以不用特别指明泛型类型, 编译器可以自动推断泛型类型.
 */

/**
 * 泛型约束
 * 1. 上限(最常见, 同 Java extend 关键字) A<T:Comparable<T>> 只有 Comparable<T> 的子类可以替换 T
 *      默认上限: Any?
 *      一个类型多个上限: 使用 where 子句(类型必须满足 where 子句的所有条件)
 */

// 一个类型, 多个上限
fun <T> copyWhenGreater(list: List<T>, threshold: T): List<String>
        where T : CharSequence,
              T : Comparable<T> {
    return list.filter { it > threshold }.map { it.toString() }
}

/**
 * 泛型擦除
 * Kotlin 的泛型安全检测于编译时完成. 运行时, 实例的具体类型信息并不会保存.
 * 例: Foo<Bar> & Foo<Baz?> 运行时被擦除为 Foo<*>
 *
 * 泛型检测以及类型转换
 * 由于泛型擦除, 没有通用的方法检测运行时的类型参数创建, 编译器也禁止使用 is-check (ints is List<Int> & list is T ...)
 * 但可以使用 Star-Projection
 * if (something is List<*>) {
 *     something.forEach{ println(it) } // 此时的类型为: List<Any?>
 * }
 * 也因此可以对非泛型部分进行 is-check / 强制类型转换(as)
 */

fun handleStrings(list: MutableList<String>) {
    if (list is ArrayList) { // is-check
        // `list` is smart-cast to `ArrayList<String>`
        TODO("测试")
    } else { // as 类型转换
        val al = list as LinkedList
    }
}

/**
 * 泛型函数的类型参数也只能用于编译时检查, 因此在函数体内不能使用类型参数的作为类型检查的依据 & 类型强制转换也是未经检测的.
 * 唯一可以使用类型参数的是带有具体类型的 inline 内联函数.
 * 但是泛型的限制仍适用于 泛型检查 以及 强制类型转换时的泛型类型实例(泛型占位的类型参数中还有泛型 -> 类型参数为 List<Int> 其中 Int 将被擦除)
 */

inline fun <reified A, reified B> Pair<*, *>.asPairOf(): Pair<A, B>? {
    if (first !is A || second !is B) return null
    return first as A to second as B
}

val somePair: Pair<Any?, Any?> = "items" to listOf(1, 2, 3)


val stringToSomething = somePair.asPairOf<String, Any>()
val stringToInt = somePair.asPairOf<String, Int>()
val stringToList = somePair.asPairOf<String, List<*>>()
val stringToStringList = somePair.asPairOf<String, List<String>>()
// Compiles but breaks type safety! 编译通过但是类型不安全

fun genericsErasure() {
    println("stringToSomething = $stringToSomething")
    // stringToSomething = (items, [1, 2, 3])
    println("stringToInt = $stringToInt")
    // stringToInt = null
    println("stringToList = $stringToList")
    // stringToList = (items, [1, 2, 3])
    println("stringToStringList = $stringToStringList")
    // stringToStringList = (items, [1, 2, 3])
    /**
     * println(stringToStringList?.second?.forEach() {it.length})
     * This will throw ClassCastException as list items are not String
     * 将抛出 CCE, 原因泛型擦除, 实际的类型为 List<Any?> Any? 没有 length 属性
     */
}

/**
 * Unchecked Casts 未经检测的类型转换 -> 编译器不能检测出来
 * 若逻辑上可行, 才可以使用 Unchecked Cast
 * 但也要避免使用 unchecked cast, 因此需要重新设计程序结构
 *
 * 泛型函数的 reified 类型参数将会像 as 一样进行强制类型转换检测
 *
 * 可以使用 @Suppress("UNCHECKED_CAST") 注解暂且消除警告
 *
 * JVM 上,例 Array<Foo> 使用 foo as Array<List<String>?> 进行强制类型转换, 只要 foo 是包含 List<*> 的 Array 就会成功,
 * 不管 foo 是否为空
 */
fun readDictionary(file: File): Map<String, *> = file.inputStream().use {
    TODO("Read a mapping of strings to arbitrary elements.")
}

// We saved a map with `Int`s into this file
val intsFile = File("ints.dictionary")

// Warning: Unchecked cast: `Map<String, *>` to `Map<String, Int>`
@Suppress("UNCHECKED_CAST")
val intsDictionary: Map<String, Int> = readDictionary(intsFile) as Map<String, Int>

/**
 * 类型操作符 _
 * 下划线操作符 _ 可以作为类型参数使用. 需要配合使用(其他类型被显式指定时, 编译器可以自动推断 _ 下划线实参的类型)
 */
abstract class SomeClass<T> {
    abstract fun execute(): T
}

class SomeImplementation : SomeClass<String>() {
    override fun execute(): String = "Test"
}

class OtherImplementation : SomeClass<Int>() {
    override fun execute(): Int = 42
}

object Runner {
    inline fun <reified S : SomeClass<T>, T> run(): T {
        return S::class.java.getDeclaredConstructor().newInstance().execute()
    }
}

fun underscore() {
    // T is inferred as String because SomeImplementation derives from SomeClass<String>
    val s = Runner.run<SomeImplementation, _>()
    assert(s == "Test")

    // T is inferred as Int because OtherImplementation derives from SomeClass<Int>
    val n = Runner.run<OtherImplementation, _>()
    assert(n == 42)
}