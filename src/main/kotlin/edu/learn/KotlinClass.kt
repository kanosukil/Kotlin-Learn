package edu.learn

import java.io.File
import javax.sql.DataSource

/**
 * @author VHBin
 * @date 2022/11/12-10:59
 */

fun main() {
    val kc = KotlinClass()
    kc.test()
}

class KotlinClass {
    fun test() {
        println("--------------------------------------------------")
        val per = Person("Lv", "HaiBin")
        println("Full Name: ${per.getFullName()}")
        val person = Person("Zhou", "Yang", "Sana丶丿")
        println("Full Name: ${person.getFullName()}")
        val pers = Person("Alex Tex")
        println("Full Name: ${pers.getFullName()}")
    }
}

/**
 * class 类
 * class 关键字声明一个类
 * 类声明包括: 类名, 类头(指定参数 + 主构造函数 + 其他), 类体(花括号包围的)
 * 类头 & 类体 可选(无类体可以省略花括号)
 */

// 类名 + 类头(指定参数以及构建的主构造函数)[constructor 关键字可省略(但在有注释修饰该类时, 不能省略)]
// 主构造函数不能包含任何代码. 初始化代码可以放在带有 init 关键字前缀的初始化器块中.
// init 代码块与 property 属性按排列顺序依次初始化
// 构造器中的参数可以用在 init 代码块以及 property 属性初始化中(其他地方不能使用)
// 亦可以在构造器参数前添加 val/var 直接初始化为该类的属性
// 也可以为构造器参数设定默认值
class Person constructor(
    firstName: String,
    secondName: String,
    private val name: String = "$secondName $firstName"
) {

    init {
        println("This is Person Class Primary Constructor.\nHello, $secondName $firstName")
    }

    val firstProperty = "First property: $firstName".also(::println)

    init {
        println("First initializer block that prints $firstName")
    }

    val secondProperty = "Second property: ${secondName.length}".also(::println)

    init {
        println("Second initializer block that prints ${secondName.length}")
    }

    // Secondary Constructor: 存在主构造函数的情况下, 每个次级构造函数都需要调用主构造函数
    constructor(name: String) : this(name, name)

    // 或指定其他构造函数
    constructor(id: Int) : this(id.toString())

    fun getFullName(): String = name
}

// 所有的次级构造函数都是委派给主构造函数执行, 而主构造函数按照 init 代码块执行
// 即使没有主构造函数, 次级构造函数也是按照 init 代码块顺序执行
class Student {
    private var name: String? = null
    private var age: Int? = null
    private val info: String?

    init {
        info = "${name ?: "Null"}, ${age ?: 0}"
    }

    // Secondary Constructor
    constructor(name: String, age: Int) {
        this.name = name
        this.age = age
    }

    // Secondary Constructor
    constructor(name: String) : this(name, 18)

}

// 没有定义任何构造函数的非抽象类将自动生成一个 public 无参构造函数
// 若不想要一个 public 构造函数, 可以在类头声明 private
class PP private constructor()

// 基于 JVM 的 Kotlin 在主构造函数的所有参数都有默认值时, 将自动生成一个额外的无参构造函数(底层按默认值设定属性)

/**
 * 类成员 Class Members
 * 1. 构造函数 & 初始化块
 * 2. 函数
 * 3. 属性
 * 4. 内嵌/内部类
 * 5. 对象声明(类似 Java 静态方法)
 */

// Kotlin 类似 Java, 所有的 class 都继承自 Any
/**
 * Any 三个方法
 * 1. equals()
 * 2. hashCode()
 * 3. toString()
 */


/**
 * 抽象类 abstract class
 * 抽象成员无实现(再其子类中实现)
 * 抽象类/抽象函数不需要 open 关键字修饰
 */
abstract class Polygon {
    abstract fun draw()
}

class Rectangle : Polygon() {
    override fun draw() {
        // draw the rectangle
    }
}

// 可以使用 open 修饰的类实现继承(kotlin 的 class 默认都是 final 标注)
open class PolygonOpen {
    open fun draw() {
        // some default polygon drawing method
    }
}

abstract class WildShape : PolygonOpen() {
    // Classes that inherit WildShape need to provide their own
    // draw method instead of using the default on Polygon
    abstract override fun draw()
}

// 继承
/**
 * 子类的构造方法都将调用父类的构造方法
 * 在子类有主构造方法时, 将自动调用父类的主构造方法
 * 在子类没有主构造方法时, 需要通过 super 关键字调用父类的构造方法
 */
/**
 * 父类中没有使用 open 修饰的方法, 子类不能重写, 也不能使用相同的签名定义方法
 * 非 open 类中的方法添加 open 无任何作用
 * 使用 open 允许子类重写; 使用 final 禁止子类重写
 */
/**
 * 子类重写父类属性, 机制上等同与方法重写
 * 父类需要使用 open 才允许子类重写属性
 * 子类需要使用 override 才可以重写父类属性, 且类型必须与父类兼容
 * 子类重写的属性可以初始化, 也可以 get() 方法重写
 * 子类可以使用 var 重写 val 属性, 但不能 val 重写 var 属性
 *      本质上 val 声明一个 get() 方法, var 又声明一个 set() 方法
 */
interface Shape {
    val vertexCount: Int
}

class RectangleShape(override val vertexCount: Int = 4) : Shape
// Always has 4 vertices

class PolygonShape : Shape {
    override var vertexCount: Int = 0
// Can be set to any number later
}

// 子类初始化顺序(创建子类实例)
// 父类初始化 -> 子类初始化
// 因此, 在子类重写的父类属性, 在子类初始化前, 都是父类中的设定值
// -> 父类初始化逻辑中使用 open 修饰的属性, 对于子类的直接/间接调用都会产生不正确行为 或 运行时异常
// 重点: open 类的初始化模块(构造函数/初始化块/属性初始化)中应该避免使用 open 成员

/**
 * 类似 Java, 子类中可以通过  super 关键字调用父类属性/方法
 * 内部类中, 可以通过 super@Outer 调用外部类的父类属性/方法
 */
open class RectangleDraw {
    open fun draw() {
        println("Drawing a rectangle")
    }

    val borderColor: String get() = "black"
}

class FilledRectangle : RectangleDraw() {
    override fun draw() {
        val filler = Filler()
        filler.drawAndFill()
    }

    inner class Filler {
        fun fill() {
            println("Filling")
        }

        fun drawAndFill() {
            super@FilledRectangle.draw() // Calls Rectangle's implementation of draw()
            fill()
            println("Drawn a filled rectangle with color ${super@FilledRectangle.borderColor}") // Uses Rectangle's implementation of borderColor's get()
        }
    }
}

/**
 * 一个类继承了多个父类/抽象类/接口
 * 若不同的父类/抽象类/接口中有相同的成员实现, 子类必须按自己的逻辑重写该成员实现.
 */
open class RectangleMultiple {
    open fun draw() { /* ... */
    }
}

interface PolygonMultiple {
    fun draw() { /* ... */
    } // interface members are 'open' by default
}

class SquareMultiple : RectangleMultiple(), PolygonMultiple {
    // The compiler requires draw() to be overridden:
    override fun draw() {
        super<RectangleMultiple>.draw() // call to Rectangle.draw()
        super<PolygonMultiple>.draw() // call to Polygon.draw()
    }
}

/**
 * Data Class 数据类
 * 创建的主要目的: 保存数据
 * 该类的标准函数&实用函数通常从数据中派生
 */

/**
 * 编译器将自动从 data class 的主构造函数中声明的属性派生以下方法:
 * 1. equals() / hashCode()
 * 2. toString() 形式: 类名(参数名=参数值,...)
 * 3. componentN() 按照声明顺序对应属性(见 Destructuring Declaration 解构声明)
 * 4. copy()
 * 数据类需要确保以下行为来保证生成代码的一致性以及行为的意义:
 * 1. 主构造函数至少一个参数
 * 2. 主构造函数的所有参数都需要使用 var/val 标识
 * 3. 数据类不能是 abstract open sealed inner
 * 对于数据类成员的继承, 数据类将按照以下规则生成代码:
 * 1. equals() hashCode() toString() 三个方法在数据类中/父类中有 final 实现, 数据类将不会再生成对应代码
 * 2. 父类中有 open 修饰且返回兼容类型的 componentN() 函数, 数据类将会生成对应的函数重写父类的对应方法
 *    若父类的对应方法由于签名不兼容/final 修饰不能重写, 数据类将会抛出一个错误.
 * 3. 不允许显示实现 componentN() & copy() 方法
 */
/**
 * 对于 Java 若需要生成无参构造函数, 就必须让主构造函数的所有参数都有默认值
 */
data class User(val name: String, val age: Int)

/**
 * 数据类体属性定义
 * 编译器只会使用主构造函数中定义的属性构造函数
 * 若不需要构造某个属性的函数, 就需要再数据类体内定义属性
 *
 * 如此, toString() equals() hashCode() copy() 的实现中, 就不会使用类体内定义的属性,
 * 只会使用主构造函数中定义的属性, 且 componentN() 也不会有对应体内属性的方法
 */

data class DataClassPerson(val name: String) {
    var age: Int = 0
}

fun dataClassInBody() {
    val person1 = DataClassPerson("John")
    val person2 = DataClassPerson("John")
    person1.age = 10
    person2.age = 20
    println("person1 == person2: ${person1 == person2}")
    // person1 == person2: true
    println("person1 with age ${person1.age}: $person1")
    // person1 with age 10: Person(name=John)
    println("person2 with age ${person2.age}: $person2")
    // person2 with age 20: Person(name=John)
}

/**
 * copy() 函数
 * 可以修改部分属性而保证其他属性不变
 * 上面 User 类中的 copy() 方法实现:
 * fun copy(name: String = this.name, age: Int = this.age) = User(name, age)
 */
// 可以如下使用 copy() 方法
val jack = User(name = "Jack", age = 1)
val olderJack = jack.copy(age = 2)

/**
 * componentN() 函数关于 Destructuring Declaration 结构声明
 * 在此不累赘
 */

/**
 * 标准数据类:
 * 1. 标准库中提供了 Pair & Triple 类,
 * 2. 自定义数据类(推荐)
 */

/**
 * Sealed Class / Interface 密封类/接口
 * 1. 有限制的层次结构
 * 2. 提供对继承的更多控制
 * 密封类在编译时已知所有的子类.
 * 任何密封类的子类都不能定义在声明密封类的 module 之外(第三方不能在他的代码中扩展你的 sealed class).
 * 密封类的所有实例在编译时都有一个确定且已知的类型.
 * (密封接口 与 密封类 一样, 一旦密封接口所在的 module 被编译了, 就不会有新的实现出现)
 *
 * 在某些情景下, 密封类与枚举类类似:
 *      枚举类的值集也是受限制的
 *      但是枚举类的实例只能是单例, 但是密封类的子类可有多个实例, 且每个实例都有自己的状态.
 *
 * 通过密封类的层次结构, 保证 module 已知所有可能的子类, 并不会出现其他子类
 */

sealed interface Error // has implementations only in same package and module

sealed class IOError : Error { // extended only in same package and module
    private constructor() : this("IOError")

    constructor(message: String) {
        println(message)
    }
}

class FileReadError(val file: File) : IOError("FileReadError")
class DatabaseError(val source: DataSource) : IOError("DatabaseError")

object RuntimeError : Error

open class CustomError : Error // can be extended wherever it's visible

/**
 * 一个 sealed class 本身就是一个抽象类, 不能直接实例化, 可以有抽象成员.
 * sealed class 的构造器只能是 protected & private 之一(默认 protected)
 */

/**
 * 直接子类的位置
 * 密封类的直接子类必须定义在同一个 package 包下, 可以是顶层声明, 也可以嵌套在命名类/命名接口/命名对象中
 * 子类的可见性任意
 * 密封类的子类必须有合适的限定名, 且不能是本地或匿名对象
 *
 * 注: 枚举类不能扩展密封类(其他类也不行), 但可以实现密封接口
 *
 * 密封类的限制对于间接子类不适用, 密封类的子类只要不是 sealed 修饰, 就可以根据可见性在任意地方扩展.
 *
 * 对于跨平台项目, 密封类的直接子类必须在同一个源集中.
 */

