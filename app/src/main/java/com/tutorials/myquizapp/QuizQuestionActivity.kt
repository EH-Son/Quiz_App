package com.tutorials.myquizapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import kotlin.random.Random

class QuizQuestionActivity : AppCompatActivity(), View.OnClickListener {

    private var mCurrentPosition : Int = 1
    private var mFinalPosition : Int = 7
    private var mQuestionsList: ArrayList<Question>? = null
    private var mQuestionNumberList: ArrayList<Int> = arrayListOf()
    private var mSelectedOptionPosition : Int = 0
    private var mSelectedOptionValue : String = ""
    private var mCorrectAnswerPosition : Int = 0
    private var mUserName : String? = null
    private var mCorrectAnswers : Int = 0
    private val random = Random(System.currentTimeMillis())
    private var mIsSelected : Boolean = false
    private var mIsCheckAnswer : Boolean = false

    private var progressBar : ProgressBar? = null
    private var tvProgress : TextView? = null
    private var tvQuestion : TextView? = null
    private var ivImage : ImageView? = null

    private var tvOptionOne : TextView? = null
    private var tvOptionTwo : TextView? = null
    private var tvOptionThree : TextView? = null
    private var tvOptionFour : TextView? = null
    private var btnSubmit : Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_question)

        mUserName = intent.getStringExtra(Constants.USER_NAME)

        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tv_progress)
        tvQuestion = findViewById(R.id.tv_question)
        ivImage = findViewById(R.id.iv_image)

        tvOptionOne = findViewById(R.id.tv_option_one)
        tvOptionTwo = findViewById(R.id.tv_option_two)
        tvOptionThree = findViewById(R.id.tv_option_three)
        tvOptionFour = findViewById(R.id.tv_option_four)
        btnSubmit = findViewById(R.id.btn_submit)

        tvOptionOne?.setOnClickListener(this)
        tvOptionTwo?.setOnClickListener(this)
        tvOptionThree?.setOnClickListener(this)
        tvOptionFour?.setOnClickListener(this)
        btnSubmit?.setOnClickListener(this)

        mQuestionsList = Constants.getQuestions()
        setQuestionNumberList()

        setQuestion()
        defaultOptionsView()
    }

    private fun setQuestionNumberList(){
        var count : Int = 0
        while(count < 7){
            var tempNum = mQuestionsList?.size?.let { random.nextInt(it) }
            if (tempNum != null) {
                if(mQuestionNumberList.contains(tempNum))
                    continue
                mQuestionNumberList.add(tempNum)
                count++
            }
        }
    }

    private fun setQuestion() {
        defaultOptionsView()
        val question: Question = mQuestionsList!![mQuestionNumberList[mCurrentPosition - 1]]
        progressBar?.progress = mCurrentPosition
        tvProgress?.text = "$mCurrentPosition/${progressBar?.max}"
        ivImage?.setImageResource(question.Image)
        tvQuestion?.text = question.question
        mIsCheckAnswer = false
        mIsSelected = false
        setOptions(question)

        setAnswerPosition(question)

        if(mCurrentPosition == mFinalPosition){
            btnSubmit?.text = "FINISH"
        }else{
            btnSubmit?.text = "SUBMIT"
        }
    }

    private fun setOptions(question: Question) {
        tvOptionOne?.text = ""
        tvOptionTwo?.text = ""
        tvOptionThree?.text = ""
        tvOptionFour?.text = ""


        var selected : Int = 0
        var randomCheck = mutableListOf<Int>()

        while(selected < 4){
            var tempNum : Int = random.nextInt(4)
            if(randomCheck.contains(tempNum))
                continue

            when(tempNum){
                0 -> {
                    if(tvOptionOne?.text.toString() == "") {
                        tvOptionOne?.text = question.options?.get(selected)
                        selected++
                        randomCheck.add(tempNum)
                    }
                }
                1 -> {
                    if(tvOptionTwo?.text.toString() == "") {
                        tvOptionTwo?.text = question.options?.get(selected)
                        selected++
                        randomCheck.add(tempNum)
                    }
                }
                2 -> {
                    if(tvOptionThree?.text.toString() == "") {
                        tvOptionThree?.text = question.options?.get(selected)
                        selected++
                        randomCheck.add(tempNum)
                    }
                }
                3 -> {
                    if(tvOptionFour?.text.toString() == "") {
                        tvOptionFour?.text = question.options?.get(selected)
                        selected++
                        randomCheck.add(tempNum)
                    }
                }
            }
        }
    }

    private fun setAnswerPosition(question: Question) {
        mCorrectAnswerPosition = when (question.correctAnswer) {
            tvOptionOne?.text.toString() -> 1
            tvOptionTwo?.text.toString() -> 2
            tvOptionThree?.text.toString() -> 3
            else -> 4
        }
    }

    private fun defaultOptionsView(){
        val options = ArrayList<TextView>()
        tvOptionOne?.let{
            options.add(0, it)
        }
        tvOptionTwo?.let{
            options.add(1, it)
        }
        tvOptionThree?.let{
            options.add(2, it)
        }
        tvOptionFour?.let{
            options.add(3, it)
        }

        for(option in options){
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(
                this,
                R.drawable.default_option_border_bg
            )
        }
    }

    private fun selectedOptionView(tv: TextView, selectedOptionNum: Int){
        if(!mIsCheckAnswer) {
            defaultOptionsView()

            mSelectedOptionPosition = selectedOptionNum
            mSelectedOptionValue = tv.text.toString()
            tv.setTextColor(Color.parseColor("#363A43"))
            tv.setTypeface(tv.typeface, Typeface.BOLD)
            tv.background = ContextCompat.getDrawable(
                this,
                R.drawable.selected_option_border_bg
            )
            mIsSelected = true
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.tv_option_one -> {
                tvOptionOne?.let {
                    selectedOptionView(it, 1)
                }
            }
            R.id.tv_option_two -> {
                tvOptionTwo?.let {
                    selectedOptionView(it, 2)
                }
            }
            R.id.tv_option_three -> {
                tvOptionThree?.let {
                    selectedOptionView(it, 3)
                }
            }
            R.id.tv_option_four -> {
                tvOptionFour?.let{
                    selectedOptionView(it, 4)
                }
            }
            R.id.btn_submit ->{
                if(mIsSelected) {
                    if (mIsCheckAnswer) {
                        mCurrentPosition++

                        when {
                            mCurrentPosition <= mFinalPosition -> {
                                setQuestion()
                            }
                            else -> {
                                val intent = Intent(this, ResultActivity::class.java)
                                intent.putExtra(Constants.USER_NAME, mUserName)
                                intent.putExtra(Constants.CORRECT_ANSWERS, mCorrectAnswers)
                                intent.putExtra(Constants.TOTAL_QUESTIONS, mFinalPosition)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } else {
                        val question = mQuestionsList!![mQuestionNumberList[mCurrentPosition - 1]]
                        if (question!!.correctAnswer != mSelectedOptionValue) {
                            answerView(mSelectedOptionPosition, R.drawable.wrong_option_border_bg)
                        } else {
                            mCorrectAnswers++
                        }
                        answerView(mCorrectAnswerPosition, R.drawable.correct_option_border_bg)
                        mIsCheckAnswer = true

                        if (mCurrentPosition == mFinalPosition) {
                            btnSubmit?.text = "FINISH"
                        } else {
                            btnSubmit?.text = "GO TO NEXT QUESTION"
                        }
                    }
                }
            }
        }
    }

    private fun answerView(answer: Int, drawableView: Int){
        when(answer){
            1 -> {
                tvOptionOne?.background = ContextCompat.getDrawable(
                    this,
                    drawableView
                )
            }
            2 -> {
                tvOptionTwo?.background = ContextCompat.getDrawable(
                    this@QuizQuestionActivity,
                    drawableView
                )
            }
            3 -> {
                tvOptionThree?.background = ContextCompat.getDrawable(
                    this@QuizQuestionActivity,
                    drawableView
                )
            }
            4 -> {
                tvOptionFour?.background = ContextCompat.getDrawable(
                    this@QuizQuestionActivity,
                    drawableView
                )
            }
        }
    }
}