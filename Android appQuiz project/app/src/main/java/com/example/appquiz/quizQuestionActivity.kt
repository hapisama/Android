package com.example.appquiz
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.appquiz.databinding.ActivityQuizQuestionBinding

class quizQuestionActivity : AppCompatActivity(), View.OnClickListener {

    private var mCurrentPosition: Int = 1
    private var mQuestionList: ArrayList<question>? = null
    private var mSelectedOptionPosition: Int = 0
    private var mCurrentAnswer: Int = 0
    private var mUserName: String? = null
    private lateinit var binding : ActivityQuizQuestionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizQuestionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mUserName = intent.getStringExtra(constants.userName)

        mQuestionList = constants.getQuestions()

        setQuestion()

        binding.optionOne.setOnClickListener(this)

        binding.optionTwo.setOnClickListener(this)

        binding.optionThree.setOnClickListener(this)

        binding.optionFour.setOnClickListener(this)

        binding.btnSubmit.setOnClickListener(this)

    }

    private fun setQuestion(){

        val question = mQuestionList!![mCurrentPosition - 1]

        defaultOptionView()

         if (mCurrentPosition == mQuestionList!!.size){
             binding.btnSubmit.text = "Finith"
             Log.d("Debug","test9")
         }else{
             binding.btnSubmit.text = "Submit"
             Log.d("Debug","test10")
         }

        binding.progressBar.progress = mCurrentPosition

        binding.tvProgress.text = "$mCurrentPosition" + "/"+ binding.progressBar.max

        binding.tvQuestion.text = question.question

        binding.imgV.setImageResource(question.image)

        binding.optionOne.text = question.optionOne
        binding.optionTwo.text = question.optionTwo
        binding.optionThree.text = question.optionThree
        binding.optionFour.text = question.optionFour

    }

    private fun defaultOptionView(){
        val options = ArrayList<TextView>()
        options.add(0, binding.optionOne)
        options.add(1, binding.optionTwo)
        options.add(2, binding.optionThree)
        options.add(3, binding.optionFour)

        for (option in options){
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(this,
            R.drawable.option_background)
        }
    }

    private fun answerView(answer: Int, drawableView: Int){
        when(answer){
            1 ->{binding.optionOne.background = ContextCompat.getDrawable(this, drawableView)}
            2 ->{binding.optionTwo.background = ContextCompat.getDrawable(this, drawableView)}
            3 ->{binding.optionThree.background = ContextCompat.getDrawable(this, drawableView)}
            4 ->{binding.optionFour.background = ContextCompat.getDrawable(this, drawableView)}

        }
        Log.d("Debug","test11")
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.optionOne ->{selectedOptionView(binding.optionOne,1)}
            R.id.optionTwo ->{selectedOptionView(binding.optionTwo,2)}
            R.id.optionThree ->{selectedOptionView(binding.optionThree,3)}
            R.id.optionFour ->{selectedOptionView(binding.optionFour,4)}
            R.id.btnSubmit -> {
                Log.d("debug", "test0")
                if(mSelectedOptionPosition == 0){
                    mCurrentPosition++
                    Log.d("Debug","test1")
                    when{
                        mCurrentPosition <= mQuestionList!!.size -> {
                            setQuestion()
                            Log.d("Debug","test2")
                        }else -> {
                            val intent = Intent(this, result::class.java)
                            intent.putExtra(constants.userName, mUserName)
                            intent.putExtra(constants.correctAnswer, mCurrentAnswer)
                            intent.putExtra(constants.totalQuestions, mQuestionList!!.size)
                            startActivity(intent)
                            finish()
                        Log.d("Debug","test3")

                        }

                    }
                }else{
                    val question = mQuestionList?.get(mCurrentPosition - 1)
                    if (question!!.correctAnswer != mSelectedOptionPosition){
                        answerView(mSelectedOptionPosition, R.drawable.wrong_option_background)
                        Log.d("Debug","test4")
                    }
                    answerView(question.correctAnswer, R.drawable.right_option_background)
                    Log.d("Debug","test5")

                    if(mCurrentPosition == mQuestionList!!.size){
                        binding.btnSubmit.text = "Finish"
                        Log.d("Debug","test6")
                    }else{
                        binding.btnSubmit.text = "Go to the next question"
                        Log.d("Debug","test7")

                    }
                    mSelectedOptionPosition = 0
                    Log.d("Debug","test8")
                }
            }
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int){
        defaultOptionView()
        mSelectedOptionPosition = selectedOptionNum
        tv.setTextColor(Color.parseColor("#363A3E"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)
        tv.background = ContextCompat.getDrawable(this,
            R.drawable.selected_option_background)
    }
}