package com.example.moneytracker

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.moneytracker.db.MoneyTrackerDb
import com.example.moneytracker.db.MoneyTrackerRepo
import com.example.moneytracker.model.LogType
import com.example.moneytracker.model.TaskLog
import io.ghyeok.stickyswitch.widget.StickySwitch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class AddTaskLogActivity : AppCompatActivity(),CoroutineScope {
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    private val moneyTrackerRepo: MoneyTrackerRepo by lazy {
        val moneyTrackerDb = MoneyTrackerDb(applicationContext)
        MoneyTrackerRepo.create(moneyTrackerDb)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_task_log)
        title = "Add Task Log"
        supportActionBar?.setDisplayShowHomeEnabled(true)
        var logType = LogType.ADD
        val btnAddTaskLogActivity = findViewById<Button>(R.id.btnAddTask)
        val stickySwitch = findViewById<StickySwitch>(R.id.stickySwitch)
        val etTaskName = findViewById<EditText>(R.id.editAddTaskName)
        val etMoney = findViewById<EditText>(R.id.editAddMoney)

        stickySwitch.setA(object : StickySwitch.OnSelectedChangeListener {
            override fun onSelectedChange(direction: StickySwitch.Direction, text: String) {
                logType = LogType.valueOf(text)
            }
            
        })
        btnAddTaskLogActivity.setOnClickListener {
                val taskName =etTaskName.text.toString()
                val money = etMoney.text.toString()

            launch {
                moneyTrackerRepo.insert(
                    TaskLog(
                        name = taskName,
                        money = money.toInt(),
                        type = logType

                    )
                )
                finish()
                setResult(1)
            }
}

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}