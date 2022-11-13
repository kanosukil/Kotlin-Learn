package edu.learn

/**
 * Kotlin 默认 import 的包:
 * 1. kotlin.*
 * 2. kotlin.annotation.*
 * 3. kotlin.collections.*
 * 4. kotlin.comparisons.*
 * 5. kotlin.io.*
 * 6. kotlin.ranges.*
 * 7. kotlin.sequences.*
 * 8. kotlin.text.*
 * Kotlin 根据依赖平台 import 的包
 * JVM: java.lang.* & kotlin.jvm.*
 * JS: kotlin.js.*
 * 注:
 * 1. 可以使用 as 为冲突包名指定别名(类 Python)
 * import example.adc
 * import learn.example.adc as learnADC
 * 2. import 还可以引入顶级函数以及属性 & 对象声明中的函数以及属性 & 枚举常量
 */
/**
 * @author VHBin
 * @date 2022/11/11-14:36
 */

class KotlinLearn(val id: Int, val name: String) {
    private val info: String = "ID:$id\nName:$name"
    private var usageNumber: Int = 0
    override fun toString(): String {
        usageNumber++
        return """
|$info
|${super.toString()}
|Has called $usageNumber times.
""".trimMargin()
    }

    fun array() {
        val list = Array(5) { i -> (i * i).toString() }
        list.forEach(System.out::println)
        val li1 = intArrayOf(1, 2, 3, 4)
        println(li1[0] + li1[3])
        val li2 = IntArray(10)
        val li3 = IntArray(10) { -2 }
        val li4 = IntArray(10) { i -> li2[i] + li3[i] + i }
        println("li2:")
        for (x in li2.indices) {
            println("li2[$x] = ${li2[x]}")
        }
        println("li3:")
        for (x in li2.indices) {
            println("li3[$x] = ${li3[x]}")
        }
        println("li4:")
        for (x in li2.indices) {
            println("li4[$x] = ${li4[x]}")
        }
    }

    fun unsignedNumber() {
        // 无符号数(主要用于需要将所有的bit位表示数的情况[十六进制RGB/本机原生API交互...])
        val ub: UByte = 1u
        val us: UShort = 1u
        val ui: UInt = 1u
        val uv = 123123u
        val uBin = 0b000101010u
        val uHex = 0xABFF_FFAAu
        val ul1: ULong = 10000000uL
        val ul2: ULong = 1000000000000UL
        print(
            """
        |$ub
        |$us
        |$ui
        |$uv
        |$uBin
        |$uHex
        |$ul1
        |$ul2
    """.trimMargin()
        )
    }

    private fun isString(str: Any?): Boolean = str is String
    private fun isNotString(str: Any?): Boolean = str !is String
    private fun stringSize(str: Any?): Int? = if (str is String) str.length else null
    private fun whenType(obj: Any?): Int = when (obj) {
        in -128..-1, in 0..127 -> {
            println("This Number is in Constant Pool")
            0
        }

        128 -> {
            println("This Number is Over Constant ranges")
            -2
        }

        is Int, Long, Short, Byte -> {
            print("Int Number Type")
            1
        }

        is String, Char -> 2
        is Array<*> -> 3
        is Float, Double -> 4
        else -> -1 // 前面使用表达式, 则 else 强制使用(除非编译器可以证明全部情况已经全部列出[枚举类...])
    }

    fun typeCheck() {
        var obj: Any? = null
        println("First: ${isString(obj)}")
        println("First: ${isNotString(obj)}")
        obj = 1
        println("Second: ${isString(obj)}")
        println("Second: ${isNotString(obj)}")
        obj = "1"
        println("Third: ${isString(obj)}")
        println("Third: ${isNotString(obj)}")
        obj = 1231231231
        println("Size1: ${stringSize(obj) ?: "Is Not String"}")
        obj = "1231231231"
        println("Size2: ${stringSize(obj)}")
        obj = 1
        println("Type: ${whenType(obj)}")
        obj = "1"
        println("Type: ${whenType(obj)}")
//        obj = intArrayOf(123, 13, 22) // -1
//        println("Type: ${whenType(obj)}")
        obj = Array(10) { 2 }
        println("Type: ${whenType(obj)}")
        obj = this
        println("Type: ${whenType(obj)}")
        // Unsafe 类型操作: as 中缀操作符
        val a: String? = try {
            obj as String?
        } catch (cce: ClassCastException) {
            "Error!"
//            null
        }
        println(a)
//        val b: String = a as String // 当 a = null 将报错
        val b: String? = obj as? String
        println(b)
    }

    fun forLoop() {
        for (i in 1..3) {
            print(i)
        }
        println()
        for (i in 1..10 step 20) {
            print(i)
        }
        println()
        for (i in 1 downTo -10) {
            print(i)
        }
        println()
        for (i in 1 downTo -100 step 20) {
            print(i)
        }
        println()
        val list = Array(5) { i -> (i * i).toString() }
        for (i in list) {
            print("$i ")
        }
        println()
        for (i in list.indices) {
            print("list[$i]=${list[i]}")
        }
        println()
        for ((i, v) in list.withIndex()) {
            print("index:$i, value:$v ")
        }
        println()
    }

    fun atMark() {
        // 只到 2,1
        breakLoop@ for (i in 1..5) {
            for (j in 1..3) {
                println("Now, (i,j)=($i,$j)")
                if (i == 2 && j == 1) {
                    break@breakLoop
                }
            }
        }
        // break 直接抛弃标记循环
        println("-------------------------------------")
        // 遇到 2,1 就跳到 3,1
        continueLoop@ for (i in 1..5) {
            for (j in 1..3) {
                println("Now, (i,j)=($i,$j)")
                if (i == 2 && j == 1) {
                    continue@continueLoop
                }
            }
        }
        // continue 抛弃当前循环, 进入下一个循环
        println("-------------------------------------")
        listOf(1, 2, 3, 4, 5).forEach returnFlag@{
            if (it == 3) return@returnFlag
            print(it)
        }
        println(" Return AtMark Test1")
        listOf(1, 2, 3, 4, 5, 6, 7, 8, 9).forEach {
            if (it % 3 == 0) return@forEach
            print(it)
        }
        println(" Return AtMark Test2")
        // return 跳出指定函数调用, 而不是直接跳出外接函数(效果同 continue)
        // 由于 lambda 函数中使用 return 直接跳出外接函数的特性, 可以使用 @ 标记
        // 也可以使用匿名函数.
        println("-------------------------------------")
        listOf(1, 23, 4, 12, 31, 3, 12, 3, 12, 31, 23, 12).forEach(
            fun(value: Int) {
                if (value % 2 == 0) {
                    return
                }
                print(value)
            })
        println(" Return Anonymous Function Test")
        // return 模拟 break + @标记
        println("-------------------------------------")
        run bMark@{
            listOf(12, 3, 123, 12, 3, 1, 23, 12, 3, 12, 3, 12, 3, 123, 1, 23, 12, 3, 12, 3).forEach {
                if (it % 3 != 0) return@bMark
                print("$it ")
            }
            println("Unreachable")
        }
        println(" Return Similar Break Mark Test")
        // 需要返回值时, 直接在后面添加即可
    }

    fun foo() {
        // lambda 中的 return 直接返回外接函数(且只有 lambda 支持)
        listOf(1, 2, 3, 4, 5).forEach {
            if (it == 3) {
                println()
                return
            } // non-local return directly to the caller of foo()
            print(it)
        }
        println("this point is unreachable")
    }

    fun exception() {
        try {
            throw Exception("Hello, this is Exception Test Function")
        } catch (ex: Exception) {
            // throw Exception(ex.message)
//            println(
//                try {
//                    readLine()?.toInt()
//                } catch (ex: Exception) {
//                    "Exception"
//                }
//            )
            println()
        } finally {
            println("This is the finally CodeBlock")
        }
    }

    @Throws(Exception::class)
    fun nothingType(obj: Any?): Nothing {
        val s = obj ?: "Null" // obj = null 时, s 为 "Null"
        // 类似于 if-else; "Null" 可以替换成其他表达式(如异常抛出)
        throw Exception("Return Nothing $s")
        // @Throws 注解标注可能抛出的异常类型
        // 返回值为 Nothing 为抛出异常的类型
        // 通过使用 Nothing 表示该方法不会有返回值
    }

    fun nothingTest(obj: Any?) {
        val str = obj ?: nothingType("obj")
        println(str)
    }

    // Nothing 类型的对象:
    // 异常, null
}

fun getName(obj: KotlinLearn?): String {
    val name = obj?.name ?: return "Name is null"
    return "Name: $name"
}

fun main() {
    val learn = KotlinLearn(1, "World")
    println(learn.toString())
    println("Learn ID: ${learn.id}\nLearn Name: ${learn.name}")
    println("Hello, ${learn.name}!")
    println(
        """
        |${learn.id}
        |$9999 "iPhone"
        |$799  RTX3080
        |$100  32GB Memory Suits
        """.trimMargin()
    )
    println("Array: ")
    learn.array()
    println("Unsigned Number: ")
    learn.unsignedNumber()
    println("Type Check: ")
    learn.typeCheck()
    println("For Loop: ")
    learn.forLoop()
    println("内嵌 Return: ")
    learn.foo()
    println("@ 标记: ")
    learn.atMark()
    println("Exception: ")
    learn.exception()
    try {
        learn.nothingType(11)
    } catch (ex: Exception) {
        println(ex.toString())
    }
    try {
        learn.nothingTest(null)
    } catch (e: Exception) {
        println(e.message)
    }

}

