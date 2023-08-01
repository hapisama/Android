package com.example.ageinminute

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import com.example.ageinminute.databinding.ActivityMainBinding
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {
        private lateinit var binding: ActivityMainBinding
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)
            binding.btnDatePicker.setOnClickListener {
                ClickDatePicker(view)
            }
        }

    fun ClickDatePicker(view: View){

        val myCalender = Calendar.getInstance()
        val year = myCalender.get(Calendar.YEAR)
        val month = myCalender.get(Calendar.MONTH)
        val dayOfMonth = myCalender.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, DatePickerDialog.OnDateSetListener {
                view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->

            val selectedDate = "$year/${month+1}/$dayOfMonth"
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd")
            val theDate = simpleDateFormat.parse(selectedDate)
            val selectedDateInMinute = theDate!!.time/60000
            val currentDate = simpleDateFormat.parse(simpleDateFormat.format(System.currentTimeMillis()))
            val currentDateInMinute = currentDate!!.time/60000
            val differenceInMinute = currentDateInMinute - selectedDateInMinute

            binding.textView3.setText(selectedDate.toString())
            binding.textView4.setText(differenceInMinute.toString())

        },year, month, dayOfMonth).show()



    }    }
