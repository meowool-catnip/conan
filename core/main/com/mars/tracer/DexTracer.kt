package com.mars.tracer

import org.jf.dexlib2.iface.DexFile

/**
 * 利用 DexLib2 解析 Dex 并执行追溯
 *
 * @author 凛
 * @github https://github.com/oh-Rin
 * @date 2020/10/15 - 07:51
 */
object DexTracer {
  fun start(dexFile: DexFile) {
    dexFile.classes
  }
}