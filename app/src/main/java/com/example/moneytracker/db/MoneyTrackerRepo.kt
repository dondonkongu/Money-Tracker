package com.example.moneytracker.db

import android.content.ContentValues
import com.example.moneytracker.model.LogType
import com.example.moneytracker.model.TaskLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MoneyTrackerRepo(private val moneyTrackerDb:MoneyTrackerDb ) {
    companion object{
        fun create(moneyTrackerDb: MoneyTrackerDb): MoneyTrackerRepo{
            return MoneyTrackerRepo(moneyTrackerDb)
        }
    }
    suspend fun insert(taskLog: TaskLog) = withContext(Dispatchers.IO){
        val database = moneyTrackerDb.writableDatabase

        ContentValues().apply {
            put(DbConfig.TaskLog.COL_TASK_NAME,taskLog.name)
            put(DbConfig.TaskLog.COL_MONEY,taskLog.money.toString())
            put(DbConfig.TaskLog.COL_TYPE,taskLog.type.toString())
        }.also {
            database.insert(DbConfig.TaskLog.TABLE_NAME,null,it)
        }
    }
    suspend fun selectAll()= withContext(Dispatchers.IO){
        val database = moneyTrackerDb.readableDatabase

        var cursor = database.query(true,DbConfig.TaskLog.TABLE_NAME,
            arrayOf(
                DbConfig.TaskLog.COL_ID,
                DbConfig.TaskLog.COL_TASK_NAME,
                DbConfig.TaskLog.COL_MONEY,
                DbConfig.TaskLog.COL_TYPE
                ),null,null,null,null,null,null)

        val result = mutableListOf<TaskLog>()
        cursor?.let {
            if (cursor.moveToFirst()) {
                do {
                    val idIndex = cursor.getColumnIndex(DbConfig.TaskLog.COL_ID)
                    val idTaskName = cursor.getColumnIndex(DbConfig.TaskLog.COL_TASK_NAME)
                    val idMoney = cursor.getColumnIndex(DbConfig.TaskLog.COL_MONEY)
                    val idType = cursor.getColumnIndex(DbConfig.TaskLog.COL_TYPE)

                    result.add(
                        TaskLog(
                            id = cursor.getInt(idIndex),
                            name = cursor.getString(idTaskName),
                            money = cursor.getString(idMoney).toInt(),
                            type = LogType.valueOf(cursor.getString(idType))
                        )
                    )

                } while (cursor.moveToNext())
            }
        }
        result
    }
    suspend fun delete(idTask: Int) = withContext(Dispatchers.IO){
        val database = moneyTrackerDb.writableDatabase
        database.delete(DbConfig.TaskLog.TABLE_NAME, "_id=$idTask",null)>0
    }



}