package com.example.appquiz
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.appquiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btmEnteredName.setOnClickListener {
            if (binding.enteredName.text.toString().isEmpty()){
                Toast.makeText(this, "please enter your name", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(it.context, quizQuestionActivity::class.java)
                intent.putExtra(constants.userName, binding.enteredName.toString())
                startActivity(intent)

            }
        }

    }
}