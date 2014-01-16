package com.klaxon.tris.storage

import scala.Array
import android.content.{ContentValues, Context}
import android.database.Cursor
import scala.annotation.tailrec

/**
 * <p>date 1/9/14 </p>
 * @author klaxon
 */
class ScoreDao(c: Context) {
  private val sqlLiteOpenHelper = new LeaderSQLiteHelper(c)

  def scores: List[Int] = {
    val db = sqlLiteOpenHelper.getReadableDatabase

    val cursor = db.query(
      LeaderSQLiteHelper.TABLE_NAME,
      Array(LeaderSQLiteHelper.SCORE_KEY),
      null, //selection, we don't have where statement
      null, //selectionArgs, we don't use selection
      null, //groupBy
      null, //having
      LeaderSQLiteHelper.SCORE_KEY + " ASC" //order by
    )

    val scores = scoresFrom(cursor)

    db.close()
    scores
  }

  private def scoresFrom(cursor: Cursor): List[Int] = {
    @tailrec
    def scoresFromRec(cursor: Cursor, scores: List[Int]): List[Int] = {
      if (cursor.isAfterLast) {
        cursor.close()
        scores
      }
      else scoresFromRec(cursor, cursor.getInt(0) :: scores)
    }

    cursor.moveToFirst()
    scoresFromRec(cursor, Nil)
  }

  def saveScore(score: Int) = {
    val db = sqlLiteOpenHelper.getWritableDatabase

    val contentValues = new ContentValues()
    contentValues.put(LeaderSQLiteHelper.SCORE_KEY, Integer.valueOf(score))

    db.insert(
      LeaderSQLiteHelper.TABLE_NAME,
      null, //hackColumns
      contentValues
    )

    db.close()
  }

  def removeScore(score: Int) = {
    val db = sqlLiteOpenHelper.getWritableDatabase

    db.delete(
      LeaderSQLiteHelper.TABLE_NAME,
      LeaderSQLiteHelper.SCORE_KEY + "=" + score,
      null
    )

    db.close()
  }

}
