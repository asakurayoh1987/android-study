package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders

// 使用包名修饰extra数据信息，这样，可避免来自不同应用的extra间发生命名冲突
private const val EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquic.answer_is_true"
private const val TAG = "CheatActivity"
private const val KEY_INDEX = "index"
const val EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquic.answer_shown"

class CheatActivity : AppCompatActivity() {
    private var answerIsTrue = false
    private var answerIsShown = false
    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var compiledVerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        answerIsShown = savedInstanceState?.getBoolean(KEY_INDEX,false) ?: false
        setAnswerShownResult(answerIsShown)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        compiledVerTextView = findViewById(R.id.compiled_version)

        compiledVerTextView.setText("API Level " + Build.VERSION.SDK_INT)

        showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }

            answerTextView.setText(answerText)
            setAnswerShownResult(true)
        }


    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putBoolean(KEY_INDEX, answerIsShown)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        answerIsShown = isAnswerShown
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }
}