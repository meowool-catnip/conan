@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-kapt` }

sourceSets["main"].java.srcDirs("main")

dependencies {
  val autoServices = "com.google.auto.service:auto-service:_"
  implementationOf(
    Kotlin.stdlib.jdk8,
    Square.kotlinPoet,
    Deps.kotlinpoetKtx,
    Deps.incap.core,
    autoServices,
    project(":annotation")
  )
  kaptOf(
    Deps.incap.processor,
    autoServices
  )
}

publishToBintray(artifact = "dextracer-kapt")
