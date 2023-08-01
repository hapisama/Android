package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.calculator.databinding.ActivityMainBinding
import kotlin.ArithmeticException

class MainActivity : AppCompatActivity() {

    var lastNumeric : Boolean = false
    var lastDot : Boolean = false

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        }



    //digits
    fun doSth(view: View) {
        binding.tvInput.append((view as Button).text)
        lastNumeric = true
    }

    fun isOperator(view: View) {
        if (lastNumeric && !isOperatorAdded(binding.tvInput.text.toString())) {
            binding.tvInput.append((view as Button).text)
            lastNumeric = false
            lastDot = false
        }
    }

    //clear
    fun Clear(view: View){
        binding.tvInput.text = ""
        lastDot = false
        lastNumeric = false
    }

    //dot
    fun onDecimalPoint(view: View){
        if (lastNumeric && !lastDot){
            binding.tvInput.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    // calculate
    fun onEqual (view: View){
        if(lastNumeric){
            var tvValue = binding.tvInput.text.toString()
            var prefix = ""
            try {
                if (tvValue.startsWith("-")){
                    prefix = "-"
                    tvValue = tvValue.substring(1)
                }

                if(tvValue.contains("-")){
                    val splitvalue = tvValue.split("-")
                    Log.d("test","test1")
                    var one = splitvalue[0]
                    Log.d("test","test2")
                    var two = splitvalue[1]
                    Log.d("test","test3")
                    if(!prefix.isEmpty()){
                        one = prefix + one
                        Log.d("test","test4")
                    }
                    Log.d("test","test5")
                    binding.tvInput.text = removingZero((one.toDouble() - two.toDouble()).toString())
                    Log.d("test","test6")
                }

                else if(tvValue.contains("+")){
                    val splitvalue = tvValue.split("+")
                    var one = splitvalue [0]
                    var two = splitvalue [1]

                    if (!prefix.isEmpty()){
                        one = prefix + one
                    }

                    binding.tvInput.text = removingZero((one.toDouble() + two.toDouble()).toString())

                }
                else if(tvValue.contains("*")){
                    val splitvalue = tvValue.split("*")
                    var one = splitvalue[0]
                    var two = splitvalue[1]

                    if(!prefix.isEmpty()){
                        one = prefix + one
                    }

                    binding.tvInput.text = removingZero((one.toDouble() * two.toDouble()).toString())
                }

                else if(tvValue.contains("/")){
                    val splitvalue = tvValue.split("/")
                    var one = splitvalue[0]
                    var two = splitvalue[1]

                    if(!prefix.isEmpty()){
                        one = prefix + one
                    }
                    if (two.toDouble() == 0.0){
                    binding.tvInput.text = (" infinity")
                    }else{
                    binding.tvInput.text = removingZero((one.toDouble() / two.toDouble()).toString())}
                }


            } catch (e: ArithmeticException) {
                e.printStackTrace()
            }
        }
    }

    //removing too much zero after dot
    private fun removingZero(result: String): String{
        var value = result
        if (result.contains(".0")){
            value = result.substring(0 , result.length -2)
        }
        return value
    }

    //showing operator

    private fun isOperatorAdded(value : String) : Boolean{
        return if (value.startsWith("-")){
            false
        }else{
            value.contains("/")
                    || value.contains("+")
                    || value.contains("*")
                    || value.contains("-")
        }

    }

}
