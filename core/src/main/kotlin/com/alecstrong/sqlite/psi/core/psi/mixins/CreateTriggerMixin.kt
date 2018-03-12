package com.alecstrong.sqlite.psi.core.psi.mixins

import com.alecstrong.sqlite.psi.core.SqliteAnnotationHolder
import com.alecstrong.sqlite.psi.core.psi.QueryElement.QueryResult
import com.alecstrong.sqlite.psi.core.psi.SqliteCompositeElementImpl
import com.alecstrong.sqlite.psi.core.psi.SqliteCreateTriggerStmt
import com.alecstrong.sqlite.psi.core.psi.SqliteTypes
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType

internal abstract class CreateTriggerMixin(
    node: ASTNode
) : SqliteCompositeElementImpl(node),
    SqliteCreateTriggerStmt {
  override fun queryAvailable(child: PsiElement): List<QueryResult> {
    if (child is MutatorMixin) {
      val table = tablesAvailable(this).first { it.tableName.name == tableName?.name }.query()
      if (hasElement(SqliteTypes.INSERT)) {
        return listOf(QueryResult(SingleRow(tableName!!, "new"), table.columns))
      }
      if (hasElement(SqliteTypes.UPDATE)) {
        return listOf(QueryResult(SingleRow(tableName!!, "new"), table.columns),
            QueryResult(SingleRow(tableName!!, "old"), table.columns))
      }
      if (hasElement(SqliteTypes.DELETE)) {
        return listOf(QueryResult(SingleRow(tableName!!, "old"), table.columns))
      }
    }
    return super.queryAvailable(child)
  }

  override fun annotate(annotationHolder: SqliteAnnotationHolder) {
    if (containingFile.triggers().any { it != this && it.triggerName.text == triggerName.text }) {
      annotationHolder.createErrorAnnotation(triggerName,
          "Duplicate trigger name ${triggerName.text}")
    }
  }

  private fun hasElement(elementType: IElementType): Boolean {
    val child = node.findChildByType(elementType) ?: return false
    return child.treeParent == node
  }
}
