package com.alecstrong.sql.psi.core

import com.intellij.icons.AllIcons
import com.intellij.lang.Language
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.tree.IFileElementType
import java.io.File

internal class TestHeadlessParser {
  private val parserDefinition = TestParserDefinition()

  fun build(root: String, annotator: SqlAnnotationHolder): SqlCoreEnvironment {
    val environment = SqlCoreEnvironment(parserDefinition, TestFileType, listOf(File(root)))
    environment.annotate(annotator)
    return environment
  }
}

private object TestLanguage : Language("Test")
private object TestFileType : LanguageFileType(TestLanguage) {
  override fun getIcon() = AllIcons.Icon
  override fun getName() = "Test File"
  override fun getDefaultExtension() = "s"
  override fun getDescription() = "Test SQLite Language File"
}

private class TestParserDefinition: SqlParserDefinition() {
  override fun createFile(p0: FileViewProvider) = TestFile(p0)
  override fun getFileNodeType() = FILE
  override fun getLanguage() = TestLanguage

  companion object {
    val FILE = IFileElementType(TestLanguage)
  }
}

private class TestFile(viewProvider: FileViewProvider) : SqlFileBase(viewProvider, TestLanguage) {
  override fun getFileType() = TestFileType
}