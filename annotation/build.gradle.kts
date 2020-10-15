@file:Suppress("SpellCheckingInspection")

plugins { kotlin }

sourceSets.main {
  java.srcDir("kotlin")
  resources.srcDir("resources")
}

dependencies.implementation(Kotlin.stdlib.jdk8)

publishToBintray(artifact = "dextracer-annotation")
