package com.example.appquiz

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import com.example.appquiz.databinding.ActivityResultBinding

private lateinit var binding: ActivityResultBinding

class result : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val userName = intent.getStringExtra(constants.userName)
        binding.userName.text = userName
        val totalQuestions = intent.getIntExtra(constants.totalQuestions, 0)
        val correctAnswer = intent.getIntExtra(constants.correctAnswer, 0)

        binding.resultTv.text = "Your score is $correctAnswer out of $totalQuestions"
        binding.finishBtn.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        //


    }
}