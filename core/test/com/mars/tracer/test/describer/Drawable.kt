package com.mars.tracer.test.describer

import com.mars.tracer.annotation.Trace
import com.mars.tracer.describer.clazz.HighSpeedClass
import com.mars.tracer.describer.common.Text
import com.mars.tracer.describer.method.Constructor
import com.mars.tracer.describer.method.models.parametersOf
import com.mars.tracer._Context
import com.mars.tracer._Drawable
import com.mars.tracer._int
import com.mars.tracer._void
import com.mars.tracer.describer.clazz.Super
import com.mars.tracer.describer.method.Method

/**
 * 模拟了一个追溯 ShapeDrawable 类的描述
 * ```
 * class Abc extends Drawable {
 *   public Abc(Context context, int res) {
 *     ...
 *     Log.i("Mars-ShapeDrawable", "Abc(context=$context, res=$res)")
 *     ...
 *   }
 *
 *   public void setColorFilter(Cba unknown, ColorFilter filter) {
 *     ...
 *   }
 * }
 * ```
 */
@Trace internal class ShapeDrawable : HighSpeedClass(
  originName = "com.rin.test.drawable.ShapeDrawable",
  noImplements = false
) {
  init {
    // ShapeDrawable 类继承了 android.graphics.Drawable
    +Super(_Drawable)
    // 此类拥有一个构造函数：第一个参数为 Context 类型, 第二个参数为 int 类型
    +Constructor(parametersOf(_Context, _int)).apply {
      // 构造函数内出现了一个 "Mars-ShapeDrawable" 字符串
      +Text("Mars-ShapeDrawable")
    }
    // 此类拥有一个 setColorFilter 方法：第一个参数为未知类型，第二个参数为 ColorFilter 类型
    +Method(
      name = "setColorFilter",
      returnType = _void,
      parameters = parametersOf(null, "Landroid/graphics/ColorFilter;")
    )
  }
}