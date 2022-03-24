package com.alecstrong.sql.psi.core

import com.alecstrong.sql.psi.test.fixtures.TestHeadlessParser
import com.intellij.psi.PsiElement
import java.io.File

internal fun compileFile(text: String, fileName: String = "temp.s"): SqlFileBase {
  val directory = File("build/tmp").apply { mkdirs() }
  val file = File(directory, fileName).apply {
    createNewFile()
    deleteOnExit()
  }
  file.writeText(text)

  val parser = TestHeadlessParser()
  val environment = parser.build(
    directory.path,
    object : SqlAnnotationHolder {
      override fun createErrorAnnotation(element: PsiElement, s: String) {
        throw AssertionError("at ${element.textOffset} : $s")
      }
    }
  )

  var result: SqlFileBase? = null
  environment.forSourceFiles {
    if (it.name == fileName) result = it
  }
  return result!!
}
