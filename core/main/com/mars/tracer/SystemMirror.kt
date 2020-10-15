@file:Suppress("ObjectPropertyName", "SpellCheckingInspection", "FunctionName", "Unused")

/**
 * 保存一些基本类的 JVM 类名
 *
 * @author 凛
 * @github https://github.com/oh-Rin
 * @date 2020/10/15 - 05:52
 */
package com.mars.tracer


const val _Boolean = "Ljava/lang/Boolean;"
const val _boolean = "Z"
const val _BooleanArray = "[Ljava/lang/Boolean;"
const val _booleanArray = "[Z"

const val _Byte = "Ljava/lang/Byte;"
const val _ByteArray = "[Ljava/lang/Byte;"
const val _byte = "B"
const val _byteArray = "[B"

const val _Character = "Ljava/lang/Character;"
const val _char = "C"
const val _CharacterArray = "[Ljava/lang/Character;"
const val _charArray = "[C"

const val _Double = "Ljava/lang/Double;"
const val _double = "D"
const val _DoubleArray = "[Ljava/lang/Double;"
const val _doubleArray = "[D"

const val _Float = "Ljava/lang/Float;"
const val _float = "F"
const val _FloatArray = "[Ljava/lang/Float;"
const val _floatArray = "[F"

const val _Integer = "Ljava/lang/Integer;"
const val _int = "I"
const val _IntegerArray = "[Ljava/lang/Integer;"
const val _intArray = "[I"

const val _Long = "Ljava/lang/Long;"
const val _long = "J"
const val _LongArray = "[Ljava/lang/Long;"
const val _longArray = "[J"

const val _Short = "Ljava/lang/Short;"
const val _short = "S"
const val _ShortArray = "[Ljava/lang/Short;"
const val _shortArray = "[S"

const val _void = "V"
const val _String = "Ljava/lang/String;"
const val _CharSequence = "Ljava/lang/CharSequence;"
const val _Class = "Ljava/lang/Class;"
const val _Object = "Ljava/lang/Object;"
const val _Throwable = "Ljava/lang/Throwable;"
const val _ObjectArray = "[Ljava/lang/Object;"
const val _ClassArray = "[Ljava/lang/Class;"
const val _Context = "Landroid/content/Context;"
const val _List = "Ljava/util/List;"
const val _Map = "Ljava/util/Map;"
const val _Comparator = "Ljava/util/Comparator;"
const val _HashMap = "Ljava/util/HashMap;"
const val _Runnable = "Ljava/lang/Runnable;"
const val _Deprecated = "Ljava/lang/Deprecated;"

const val _Bundle = "Landroid/os/Bundle;"
const val _Activity = "Landroid/app/Activity;"
const val _Typeface = "Landroid/graphics/Typeface;"
const val _Handler = "Landroid/os/Handler;"
const val _Handler_Callback = "Landroid/os/Handler\$Callback;"
const val _Intent = "Landroid/content/Intent;"
const val _Drawable = "Landroid/graphics/Drawable;"


const val _View = "Landroid/view/View;"
const val _ViewGroup = "Landroid/view/ViewGroup;"
const val _BaseAdapter = "Landroid/widget/BaseAdapter;"
const val _Editable = "Landroid/text/Editable;"

const val _Observable = "Ljava/util/Observable;"

inline fun <reified T> _array() = "[L${T::class.java.canonicalName.replace(".", "/")};"