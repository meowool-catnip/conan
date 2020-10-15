@file:Suppress("SpellCheckingInspection")

plugins { kotlin; `kotlin-kapt` }

sourceSets {
  val generatedDir = "${buildDir.absolutePath}/generated/source/kaptKotlin/"
  main.java.srcDirs("main", "$generatedDir/main")
  test.java.srcDirs("test", "$generatedDir/test")
}

dependencies {
  importSharedDependencies(this, "kotlin")
  kaptProjects(":kapt")
  kaptTestProjects(":kapt")
  apiProjects(":annotation")
  apiOf(Deps.dexlib2, Deps.multidexlib2)
}

publishToBintray(artifact = "dextracer-core")