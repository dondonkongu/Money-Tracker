package com.example.moneytracker.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneytracker.R
import com.example.moneytracker.db.MoneyTrackerDb
import com.example.moneytracker.db.MoneyTrackerRepo
import com.example.moneytracker.model.LogType
import com.example.moneytracker.model.TaskLog

class LogAdapter(private val dataset: MutableList<TaskLog> = mutableListOf(),
        private val moneyTrackerRepo: MoneyTrackerRepo)
    :RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    fun setData(dataset: MutableList<TaskLog>){
        this.dataset.clear()
        this.dataset.addAll(dataset)
        notifyDataSetChanged()
    }

    inner class LogViewHolder(view:View): RecyclerView.ViewHolder(view){
        private val tvTaskName: TextView = view.findViewById(R.id.tvTaskName)
        private val tvMoney: TextView = view.findViewById(R.id.tvMoney)
        fun bind(task:TaskLog){
            tvTaskName.text= task.name
            if (task.type===LogType.ADD){
                tvMoney.apply {
                    setTextColor(Color.parseColor("#669900"))
                    text = "+ ${task.money}"
                }
            }else{
                tvMoney.apply {
                    setTextColor(Color.RED)
                    text = "- ${task.money}"
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_log,parent,false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(dataset[position])
    }

    override fun getItemCount()= dataset.size

}