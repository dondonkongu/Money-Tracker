package com.example.moneytracker

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moneytracker.adapter.LogAdapter
import com.example.moneytracker.db.MoneyTrackerDb
import com.example.moneytracker.db.MoneyTrackerRepo
import com.example.moneytracker.model.LogType
import com.example.moneytracker.model.TaskLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(),CoroutineScope {
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job +Dispatchers.Main

    private val moneyTrackerRepo: MoneyTrackerRepo by lazy {
        val moneyTrackerDb = MoneyTrackerDb(applicationContext)
        MoneyTrackerRepo.create(moneyTrackerDb)
    }
    private lateinit var logAdapter: LogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Money Tracker"
        // khoi tao resutlauncher

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
                result ->
                if (result.resultCode === Activity.RESULT_OK){
                    val data = result.data
                    val taskName = data?.getBundleExtra("task_name")
                    Toast.makeText(this,"Task: $taskName", Toast.LENGTH_SHORT).show()
                }
            }

        val recyclerView = findViewById<RecyclerView>(R.id.rcl)

        var linearLayoutManager = LinearLayoutManager(applicationContext)
        val decoration = DividerItemDecoration(this, linearLayoutManager.orientation)
        ContextCompat.getDrawable(this, R.drawable.bg_divider)

         logAdapter =LogAdapter(moneyTrackerRepo = moneyTrackerRepo)

        recyclerView.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(decoration)
            adapter=logAdapter
        }
        loadTask(true)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)
        if (requestCode==1){
            loadTask(false)
        }
    }



    private fun loadTask(enableDelay: Boolean){
        launch{
            if (enableDelay){
                delay(2000L)

            }
            val taskLists = moneyTrackerRepo.selectAll()

            withContext(Dispatchers.Main){
                if(taskLists.isEmpty()){
                    showEmptyTask()
                }else{
                    displayTasks(taskLists)
                }
            }
        }
    }
    private fun showEmptyTask(){
        val container = findViewById<LinearLayout>(R.id.trackMoneyContainer)
        val loading = findViewById<ProgressBar>(R.id.loading)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        loading.visibility= View.GONE
        container.visibility = View.GONE
        tvStatus.visibility=View.VISIBLE
        tvStatus.text = "Empty !"
    }
    private fun displayTasks(listTasks: MutableList<TaskLog>){
        val container = findViewById<LinearLayout>(R.id.trackMoneyContainer)
        val loading = findViewById<ProgressBar>(R.id.loading)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        loading.visibility= View.GONE
        tvStatus.visibility=View.GONE
        container.visibility = View.VISIBLE
        logAdapter.setData(listTasks)

    }

    fun mockTaskLogs(): MutableList<TaskLog> {
        val rs = mutableListOf<TaskLog>()

        for (item in 1..15) {
            if (item % 2 == 0) {
                rs.add(
                    TaskLog(
                        id = item,
                        name = "item $item",
                        money = 200 * item,
                        type = LogType.ADD
                    )
                )
            }else{
                rs.add(
                    TaskLog(
                        id = item,
                        name = "item $item",
                        money = 200 * item,
                        type = LogType.SUBTRACT
                    )
                )
            }

        }
        return rs
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item?.itemId==R.id.addTaskLog){
            val intent = Intent(this,AddTaskLogActivity::class.java)
            resultLauncher.launch(intent)
            return true
        }
        return  super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}