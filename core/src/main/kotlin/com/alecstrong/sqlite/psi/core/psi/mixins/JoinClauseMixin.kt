package com.alecstrong.sqlite.psi.core.psi.mixins

import com.alecstrong.sqlite.psi.core.psi.QueryElement.QueryResult
import com.alecstrong.sqlite.psi.core.psi.SqliteCompositeElementImpl
import com.alecstrong.sqlite.psi.core.psi.SqliteJoinClause
import com.alecstrong.sqlite.psi.core.psi.SqliteJoinConstraint
import com.alecstrong.sqlite.psi.core.psi.SqliteTypes
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement

abstract internal class JoinClauseMixin(
    node: ASTNode
) : SqliteCompositeElementImpl(node),
    SqliteJoinClause {
  override fun queryAvailable(child: PsiElement): List<QueryResult> = analyze("queryAvailable") {
    if (child is SqliteJoinConstraint) {
      var queryAvailable = tableOrSubqueryList[0].queryExposed()
      tableOrSubqueryList.drop(1).zip(joinConstraintList)
          .forEach { (subquery, constraint) ->
            if (child == constraint) {
              if (child.node.findChildByType(SqliteTypes.USING) != null) {
                return listOf(QueryResult(null, queryAvailable.flatMap { it.columns }
                    .filterIsInstance<PsiNamedElement>()
                    .filter {
                      it.name!! in subquery.queryExposed()
                          .flatMap { it.columns }
                          .filterIsInstance<PsiNamedElement>()
                          .mapNotNull { it.name }
                    }))
              }
              return queryAvailable + subquery.queryExposed()
            }
            queryAvailable += subquery.queryExposed()
          }
      return queryExposed()
    }
    return super.queryAvailable(child)
  }

  override fun queryExposed(): List<QueryResult> = analyze("queryExposed") {
    var queryAvailable = tableOrSubqueryList[0].queryExposed()
    tableOrSubqueryList.drop(1)
        .zip(joinConstraintList)
        .zip(joinOperatorList) zip2@{ (subquery, constraint), operator ->
          queryAvailable += subquery.queryExposed().let { query ->
            when {
              query.isEmpty() -> return@zip2
              else -> QueryResult(
                  table = query.first().table,
                  columns = query.flatMap { it.columns },
                  joinOperator = operator,
                  joinConstraint = constraint
              )
            }
          }
        }
    return queryAvailable
  }
}