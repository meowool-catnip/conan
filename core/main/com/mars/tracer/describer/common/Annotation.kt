@file:Suppress("DuplicatedCode", "MemberVisibilityCanBePrivate", "SpellCheckingInspection")

package com.mars.tracer.describer.common

import com.mars.tracer.describer.Describer
import org.jf.dexlib2.iface.value.ArrayEncodedValue
import org.jf.dexlib2.iface.value.StringEncodedValue
import org.jf.dexlib2.iface.Annotation as _Annotation


/**
 * 描述注解
 *
 * @param type 注解类型路径
 * @sample
 * ```
 * .annotation build La/b/Class;
 * .end annotation
 * ```
 */
internal open class Annotation(val type: String) : Describer<_Annotation>() {
  override fun match(sponsor: _Annotation) = sponsor.type == type

  override fun toString(): String = """
    Annotation(type="$type", parentInfo={
      ${super.toString()}
    })
  """.trimIndent()
}

/**
 * 描述系统虚拟机签名注解（一般用于泛型？）
 *
 * @param value 签名数组
 * @param valueString 合并后的数组的字符串
 * @sample
 * ```
 * .annotation system Ldalvik/annotation/Signature;
 *     value = {
 *         "(",
 *         "Ljava/lang/String;",
 *         "IIZ)",
 *         "Ljava/util/List",
 *         "<",
 *         "La/b/Class;",
 *         ">;"
 *     }
 * .end annotation
 * ```
 */
internal class SignatureAnnotation(
  val valueString: String? = null,
  val value: Array<String?>? = null
) : Annotation(type = "Ldalvik/annotation/Signature;") {
  override fun match(sponsor: _Annotation) =
    super.match(sponsor) && sponsor.elements.first().let { element ->
      val elementValue = element.value
      if (element == null || element.name != "value" || elementValue !is ArrayEncodedValue) return false

      // 得到注解中的 value 数组
      val array = elementValue.value

      // 将注解中的数组合并成字符串，如果字符串不对等则匹配失败
      if (valueString != null && array.joinToString("") { (it as StringEncodedValue).value } != valueString) return false

      if (value != null) {
        if (array.size != value.size) return false
        // 对比数组中的内容
        array.forEachIndexed { index, encodedValue ->
          val describe = value[index]
          // 如果描述的数组内容中存在 null 则可以代表忽略此下标，但是如果不为 null 且不对等则匹配失败
          if (describe != null && describe != (encodedValue as StringEncodedValue).value) return false
        }
      }
      true
    }

  override fun toString(): String = """
    SignatureAnnotation(valueString=$valueString, value=${value?.contentToString()}, parentInfo={
      ${super.toString()}
    })
  """.trimIndent()
}