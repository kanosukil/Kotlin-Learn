package edu.learn

import java.util.*

/**
 * @author VHBin
 * @date 2022/11/12-22:36
 */

/**
 * 不通过继承 或 设计模式的装饰器模式, 使用一个新的函数性声明扩展一个类或一个接口
 * 扩展的特殊声明
 * 例: 编写函数扩展不能修改的第三方库中的类/接口, 之后仍可以像使用原始的类方法一样.
 */
class KotlinExtensions

/**
 * 函数扩展
 * 函数名前添加接收者类型
 */

// 扩展 MutableList 一个 swap 函数
// this 关键字表示为接收者类型的对象
// 下仅为 MutableList<Int> 扩展
//fun MutableList<Int>.swap(index1: Int, index2: Int) {
//    val tmp = this[index1] // 'this' corresponds to the list
//    this[index1] = this[index2]
//    this[index2] = tmp
//}
// 下为 MutableList<T> 泛型扩展
fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1] // 'this' corresponds to the list
    this[index1] = this[index2]
    this[index2] = tmp
}

fun main() {
    val list = mutableListOf(1, 2, 3)
    list.forEach(::print)
    list.swap(0, 2) // 'this' inside 'swap()' will hold the value of 'list'
    println()
    list.forEach(::print)
    companionObjectExtension()
}

/**
 * 扩展解析的静态性
 * 扩展并没有修改对应类, 只是通过 . 点表示法使定义的函数可用, 并不会插入的对应类中成为新成员
 *
 * 由于扩展函数的静态性, 意味着扩展函数并不是按照接收者类型分配的虚函数,
 * 也不是在运行时才根据表达式的类型决定结果, 而是编译时就决定了该函数对象的类型
 */
fun extensionStatic() {
    open class Shape
    class Rectangle : Shape()

    fun Shape.getName() = "Shape"
    fun Rectangle.getName() = "Rectangle"

    fun printClassName(s: Shape) {
        println(s.getName())
    }

    printClassName(Rectangle())
    // 输出 Shape, printClassName 的传入参数是 Shape 类型, 因此返回也是 Shape 类型的 getName()
}

/**
 * 扩展不能重写已有成员函数
 */

fun existsMember() {
    class Example {
        fun printFunctionType() {
            println("Class method")
        }
    }

    fun Example.printFunctionType() {
        println("Extension function")
    }

    Example().printFunctionType()
    // 输出 Class method
    // 原因: 成员方法默认修饰符: public final fun ...
    // final 不能被重写
    // 但是可以重载
    fun Example.printFunctionType(date: Date) {
        println("Extension function: $date")
    }

    Example().printFunctionType(Date())
}

/**
 * 可空接收者
 * 在函数内使用 this == null 检测接收者是否为空
 */

fun Any?.toString(): String {
    if (this == null) return "null"
    // after the null check, 'this' is autocast to a non-null type,
    // so the toString() below resolves to the member function of the Any class
    // null 检测之后, this 将自动转换成非空类型, 因此 toString() 将被解析为 Any 类的成员方法
    return toString()
}

/**
 * 属性扩展(像函数扩展一样)
 * 由于扩展不会插入到接收者的类中, 因此扩展的属性没有 backing field, 也就不允许初始化扩展属性
 * 只能通过显示的定义 getter/setter 决定属性的行为
 */
val <T> List<T>.lastIndex: Int
    get() = size - 1

// val String.help: Int = 123
// 报错: Extension property cannot be initialized because it has no backing field

/**
 * 伴随对象扩展 Companion Object
 * 伴随对象可以扩展方法 & 属性
 */
class MyClass {
    companion object { // will be called "Companion"
        var callNumber: Int = 0
    }
}

fun companionObjectExtension() {
    fun MyClass.Companion.printCompanion() {
        callNumber++
        println("companion: has called $callNumber times")
    }
    MyClass.Companion.printCompanion()
    MyClass.printCompanion()
}

/**
 * 可以使用扩展的范围
 * + 大多数情况下, 可以直接在顶层定义扩展
 *      在其他包下, 可以 import 后, 直接使用扩展
 */

/**
 * 可以在一个类中声明另一个类的扩展
 * 此类扩展体内
 * 1. 可以不使用限定符就可以使用接收器类的属性/成员
 * 2. 也可以不使用限定符就可以访问当前类的属性/成员
 * 3. 使用两个类有冲突的属性/成员时, 默认使用扩展类的属性/成员
 *    可以通过 this@类名 的方式使用声明扩展类的属性成员
 * 声明该扩展的类中, 可以调用该扩展
 * 但是在声明扩展类之外, 就不能通过扩展类使用扩展方法
 * 扩展类 = 扩展接收器
 * 声明扩展类 = 分派接收器
 * 该扩展方法可以被声明为 open 并被子类重写
 * (对于分派接收器, 扩展函数是虚的, 可以被子类重写)
 * (对于扩展接收器, 扩展函数是静态的, 声明时是什么类型, 就调用什么类型的扩展)
 */

class Host(val hostname: String) {
    fun printHostname() {
        print(hostname)
    }
}

class Connection(val host: Host, val port: Int) {
    fun printPort() {
        print(port)
    }

    fun Host.printConnectionString() {
        printHostname()   // calls Host.printHostname()
        print(":")
        printPort()   // calls Connection.printPort()
    }

    fun connect() {
        /*...*/
        host.printConnectionString()   // calls the extension function
    }

    // 若两个类之间的方法有冲突, 默认使用扩展类的方法
    // 而声明扩展的类的方法可以通过 this@类名 的方式指明
    fun Host.getConnectionString() {
        toString()         // calls Host.toString()
        this@Connection.toString()  // calls Connection.toString()
    }
}

fun declareExtension() {
    Connection(Host("kotlin.edu"), 443).connect()
    // Host("kotlin.edu").printConnectionString()
    // error, the extension function is unavailable outside Connection
}


// open 修饰扩展方法
open class Base

class Derived : Base()

open class BaseCaller {
    open fun Base.printFunctionInfo() {
        println("Base extension function in BaseCaller")
    }

    open fun Derived.printFunctionInfo() {
        println("Derived extension function in BaseCaller")
    }

    fun call(b: Base) {
        b.printFunctionInfo()   // call the extension function
    }
}

class DerivedCaller : BaseCaller() {
    override fun Base.printFunctionInfo() {
        println("Base extension function in DerivedCaller")
    }

    override fun Derived.printFunctionInfo() {
        println("Derived extension function in DerivedCaller")
    }
}

fun openExtension() {
    BaseCaller().call(Base())
    // "Base extension function in BaseCaller"
    DerivedCaller().call(Base())
    // "Base extension function in DerivedCaller" - dispatch receiver is resolved virtually
    DerivedCaller().call(Derived())
    // "Base extension function in DerivedCaller" - extension receiver is resolved statically
}

/**
 * 对于扩展的可见性
 * 1. 顶层扩展可以使用同文件的 private 声明方法/属性
 * 2. 定义在接收器之外的扩展方法, 不能使用接收器的 private & protected 方法/属性
 */