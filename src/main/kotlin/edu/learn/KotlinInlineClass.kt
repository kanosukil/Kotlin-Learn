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
 * 基于 JVM 的 Kotlin, 使用 value 定义一个内联类, 同时需要 @JvmInline 注解修饰(使用 inline 修饰已被弃用)
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

/**
 * 继承性:
 * inline class 内联类允许继承接口
 * 但不允许参与类的层次结构(不允许继承/扩展其他类) -> inline class 一直都是 final
 */

interface Printable {
    fun prettyPrint(): String
}

@JvmInline
value class PrintableName(val s: String) : Printable {
    override fun prettyPrint(): String = "Let's $s!"
}

/**
 * inline class 通过 Kotlin 编译器编译后, 都会存留一个包装器.
 * inline class 实例在运行时可以表现为包装器 或者是 基础类型 -> 类似于 Kotlin 的 Int 表现为 基础类型 int 与 包装类 Integer
 *
 * Kotlin 的编译器为了优化并生成性能更好的代码, 优先生成基础类型而不是包装器(有必要生成包装器时还是会生成为包装器的).
 * inline class 在被作为另一种类型使用时, 将被装箱 Boxed
 */
interface I

@JvmInline
value class Foo(val i: Int) : I

fun asInline(f: Foo) {
    println("f is ${f.javaClass}")
}

fun <T> asGeneric(x: T) {
    println("x is Foo: ${x is Foo}")
}

fun asInterface(i: I) {
    println("i is ${i.javaClass}")
}

fun asNullable(i: Foo?) {
    println("i is ${i?.javaClass ?: "null"}")
}

fun <T> id(x: T): T = x // 若传入的是 inline 类, 传入时装箱, 返回时拆箱

/**
 * inline class 既可以表示基础数据, 也可以表示包装类 -> 不能进行引用相等(没有意义)
 *
 * inline class 也可以有泛型参数: 但此时, 编译器将其映射为 Any? 视为类型参数的上界.
 * (由于 inline class 泛型是实验性特性, 因此需要在编译时使用: -language-version 1.8)
 */
//@JvmInline
//value class UserId<T>(val value: T)
//
//fun compute(s: UserId<String>) {} // compiler generates fun compute-<hashcode>(s: Any?)

/**
 * Mangling
 * inline class 编译后一般都是作为基础数据类型, 因此可能导致各种模糊错误(平台签名冲突):
 * @JvmInline
 * value class UInt(val x: Int)
 *
 * // Represented as 'public final void compute(int x)' on the JVM
 * fun compute(x: Int) { }
 *
 * // Also represented as 'public final void compute(int x)' on the JVM!
 * fun compute(x: UInt) { }
 *
 * 上述两个函数在 JVM 的表示相同
 * 为了减轻(非避免/消除)此现象, 使用内联类的函数将在函数名后添加稳定的 HashCode
 * 因此使用了内联类的函数编译后, 函数名之后将会添加 HashCode
 * 如上述的 fun compute(x: UInt){ } 将会编译成 public final void compute-<hashcode>(int x) 来解决冲突
 *
 * 注: mangling 策略在 Kotlin 1.4.30 发生修改, 需要使用编译参数 -Xuse-14-inline-classes-mangling-scheme
 * 来强制编译器使用 1.4.0 的旧策略来保证程序的二进制兼容性
 */

/**
 * Java 代码调用 kotlin inline Class 函数
 * 使用 @JvmName("JavaCodeFunctionName") 注解
 */
@JvmInline
value class XInt(val x: Int)

fun compute(x: Int) {}

@JvmName("computeUInt")
fun compute(x: XInt) {
}

/**
 * inline class 内联类 与 type aliases 类型别名
 * 两者的区别在于:
 *  类型别名与基础类型在赋值上兼容, 但是内联类不兼容.
 *  (类型别名的使用与原始类型一致, 而内联类不能按照原始类型的使用方法使用)
 * 内联类引入了一个真正的新类型, 类型别名只是就现有类型简化/优化命名而已(未创建一个新类型).
 */

typealias NameTypeAlias = String

@JvmInline
value class NameInlineClass(val s: String)

fun acceptString(s: String) {}
fun acceptNameTypeAlias(n: NameTypeAlias) {}
fun acceptNameInlineClass(p: NameInlineClass) {}

/**
 * inline class 委派
 * 委派 inline class 的 内联值(传入参数) 来实现接口.
 */
interface MyInterface {
    fun bar()
    fun foo() = "foo"
}

@JvmInline
value class MyInterfaceWrapper(val myInterface: MyInterface) : MyInterface by myInterface

fun inlineDelegation() {
    val my = MyInterfaceWrapper(object : MyInterface {
        override fun bar() {
            // TODO("测试内联类的委派机制")
        }
    })
    println(my.bar())
    println(my.foo())
}

fun main() {
    val name = Name("Kotlin")
    name.greet() // method `greet` is called as a static method
    // greet() 作为一个静态方法调用
    println(name.length) // property getter is called as a static method
    // 属性的 getter 作为一个静态方法调用
    val printableName = PrintableName("Kotlin")
    println(printableName.prettyPrint()) // Still called as a static method
    // 仍然作为一个静态方法调用

    val f = Foo(42)
    asInline(f)    // unboxed: used as Foo itself 不装箱(Foo -> Foo)
    asGeneric(f)   // boxed: used as generic type T 装箱(Foo -> T 泛型)
    asInterface(f) // boxed: used as type I 装箱(Foo -> I 父接口)
    asNullable(f)  // boxed: used as Foo?, which is different from Foo 装箱(Foo -> Foo?)
    // below, 'f' first is boxed (while being passed to 'id') and then unboxed (when returned from 'id')
    // In the end, 'c' contains unboxed representation (just '42'), as 'f'
    // f 先作为 id 装箱, 再作为 id 返回时拆箱; 最终变量 c 包含未拆箱表示的 f
    val c = id(f)
    println(c)

    // inline class & type aliases
    val nameAlias: NameTypeAlias = ""
    val nameInlineClass: NameInlineClass = NameInlineClass("")
    val string: String = ""
    // 形参是基础类型:
    acceptString(nameAlias) // OK: pass alias instead of underlying type
    // acceptString(nameInlineClass) // Not OK: can't pass inline class instead of underlying type
    // 形参是类型别名 & 内联类
    acceptNameTypeAlias(string) // OK: pass underlying type instead of alias
    // acceptNameInlineClass(string) // Not OK: can't pass underlying type instead of inline class
    // 虽然编译器将内联类编译为基础类型, 但是在编写时不能混为一谈


    inlineDelegation()
}