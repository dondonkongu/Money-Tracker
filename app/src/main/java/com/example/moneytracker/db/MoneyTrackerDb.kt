package com.example.moneytracker.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbConfig {
    companion object {
        const val DB_NAME= "app-money-tracker.db"
        const val DB_VERSION= 1
    }
    object TaskLog{
        const val TABLE_NAME = "task_log"
        const val COL_ID ="_id"

        const val COL_TASK_NAME ="task_name"
        const val COL_MONEY ="money"
        const val COL_TYPE ="type"

        fun buildSchema() ="""
            CREATE TABLE $TABLE_NAME(
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_TASK_NAME TEXT,
            $COL_MONEY TEXT,
            $COL_TYPE TEXT
            )
            """
        fun dropTable()= "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
class MoneyTrackerDb : SQLiteOpenHelper{
    constructor(context: Context):super(context, DbConfig.DB_NAME, null, DbConfig.DB_VERSION)

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(DbConfig.TaskLog.buildSchema())
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL(DbConfig.TaskLog.dropTable())
        p0?.execSQL(DbConfig.TaskLog.buildSchema())
    }
}