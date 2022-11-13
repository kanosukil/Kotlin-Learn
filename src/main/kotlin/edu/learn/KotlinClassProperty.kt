package edu.learn

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