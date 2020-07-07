package com.alecstrong.sql.psi.core

import com.google.common.truth.Truth.assertThat
import java.io.File
import org.junit.After
import org.junit.Before
import org.junit.Test

class NullabilityTest {
  @Before
  fun before() {
    File("build/tmp").deleteRecursively()
  }

  @After
  fun after() {
    SqlParserUtil.reset()
    File("build/tmp").deleteRecursively()
  }

  @Test fun outerJoin() {
    DialectPreset.SQLITE_3_18.setup()
    val file = compileFile("""
      |CREATE TABLE car (
      |  _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
      |  id TEXT NOT NULL,
      |  brand TEXT NOT NULL
      |);
      |
      |CREATE TABLE owner (
      |  _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
      |  name TEXT NOT NULL,
      |  carId TEXT
      |);
      |
      |SELECT *
      |FROM (SELECT owner.name, car.brand AS carBrand, car.rowid AS rowid
      |      FROM owner
      |      LEFT OUTER JOIN car ON owner.carId = car.id)
      |WHERE carBrand = ?;
    """.trimMargin())

    val select = file.sqlStmtList!!.stmtList.mapNotNull { it.compoundSelectStmt }.single()
    val projection = select.queryExposed().flatMap { it.columns }

    assertThat(projection[0].nullable).isFalse()
    assertThat(projection[1].nullable).isTrue()
    assertThat(projection[2].nullable).isTrue()
  }

  @Test fun leftJoinGroupBy() {
    DialectPreset.SQLITE_3_18.setup()
    val file = compileFile("""
      |CREATE TABLE target (
      |  id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
      |  coacheeId INTEGER NOT NULL,
      |  name TEXT NOT NULL
      |);
      |
      |CREATE TABLE challengeTarget (
      |  targetId INTEGER NOT NULL,
      |  challengeId INTEGER NOT NULL
      |);
      |
      |CREATE TABLE challenge (
      |  id INTEGER NOT NULL,
      |  cancelledAt INTEGER,
      |  emoji TEXT
      |);
      |
      |SELECT target.id AS id, target.name AS name, challenge.emoji
      |  FROM target
      |  LEFT JOIN challengeTarget
      |    ON challengeTarget.targetId = target.id
      |  LEFT JOIN challenge
      |    ON challengeTarget.challengeId = challenge.id AND challenge.cancelledAt IS NULL
      |  WHERE target.coacheeId = ?
      |  GROUP BY 1
      |  ORDER BY target.name COLLATE NOCASE ASC
      |;
    """.trimMargin())

    val select = file.sqlStmtList!!.stmtList.mapNotNull { it.compoundSelectStmt }.single()
    val projection = select.queryExposed().flatMap { it.columns }

    assertThat(projection[0].nullable).isFalse()
    assertThat(projection[1].nullable).isFalse()
    assertThat(projection[2].nullable).isTrue()
  }
}
