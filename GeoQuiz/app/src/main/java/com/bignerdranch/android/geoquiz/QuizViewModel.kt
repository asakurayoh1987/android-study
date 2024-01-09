package com.bignerdranch.android.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {
    var currentIndex = 0
    private var remainCheatTimes = 3

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    val currentCheated: Boolean
        get() = questionBank[currentIndex].cheated

    val cheatEnable: Boolean
        get() = remainCheatTimes > 0

    fun moveToNext() {
        Log.d(TAG, "Move to Next")
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun cheat() {
        if (!questionBank[currentIndex].cheated) {
            questionBank[currentIndex].cheated = true
            remainCheatTimes--
        }

    }
}