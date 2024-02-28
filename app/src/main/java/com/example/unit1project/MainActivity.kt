package com.example.unit1project

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import nl.dionsegijn.konfetti.xml.KonfettiView
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private var attempts = 0
    private lateinit var allTextViews: Array<TextView?>
    private lateinit var actualWord: String
    private lateinit var userInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val confetti = findViewById<KonfettiView>(R.id.konfettiView)

        val guess1TextView = findViewById<TextView>(R.id.guess1)
        val guess1CheckTextView = findViewById<TextView>(R.id.guess1Check)
        val guess2TextView = findViewById<TextView>(R.id.guess2)
        val guess2CheckTextView = findViewById<TextView>(R.id.guess2Check)
        val guess3TextView = findViewById<TextView>(R.id.guess3)
        val guess3CheckTextView = findViewById<TextView>(R.id.guess3Check)

        val guess1Entry = findViewById<TextView>(R.id.guess1Entry)
        val guess2Entry = findViewById<TextView>(R.id.guess2Entry)
        val guess3Entry = findViewById<TextView>(R.id.guess3Entry)

        val guess1Result = findViewById<TextView>(R.id.guess1Result)
        val guess2Result = findViewById<TextView>(R.id.guess2Result)
        val guess3Result= findViewById<TextView>(R.id.guess3Result)

        val wordle = findViewById<TextView>(R.id.wordle)
        userInput = findViewById<EditText>(R.id.editTextText)
        val submitButton = findViewById<Button>(R.id.submit)
        val resetButton = findViewById<Button>(R.id.reset)


        allTextViews = arrayOf(guess1TextView,
            guess1CheckTextView,
            guess2TextView,
            guess2CheckTextView,
            guess3TextView,
            guess3CheckTextView,
            guess1Entry,
            guess1Result,
            guess2Entry,
            guess2Result,
            guess3Entry,
            guess3Result,
            wordle,
            )


        reset()

        submitButton.setOnClickListener{
            // select all the entered text so that the user doesn't have enter backspace
//            userInput.requestFocus()
            userInput.selectAll()
            val enteredText = userInput.text.toString().uppercase()
            if (enteredText == ""){
                Toast.makeText(applicationContext, "Enter something first", Toast.LENGTH_SHORT).show()
            } else{
                if (enteredText.length > 4){
                    Toast.makeText(applicationContext, "Enter only 4 characters", Toast.LENGTH_SHORT).show()
                } else if (checkNonAlphabetic(enteredText)){
                    Toast.makeText(applicationContext, "Enter characters ONLY", Toast.LENGTH_SHORT).show()
                } else{
                    hideKeyboard()
                    attempts ++
                    val result = checkAnswer(actualWord, enteredText)
                    val colorfulResult = getColorfulString(enteredText, result)
                    Toast.makeText(applicationContext, actualWord, Toast.LENGTH_LONG).show()

                    when (attempts) {
                        1 -> { // show guess 1 and its result
                            guess1TextView.visibility = View.VISIBLE
                            guess1CheckTextView.visibility = View.VISIBLE
                            guess1Entry.text = enteredText
                            guess1Result.text = colorfulResult // result
                            guess1Entry.visibility = View.VISIBLE
                            guess1Result.visibility = View.VISIBLE
                        }
                        2 -> {
                            guess2TextView.visibility = View.VISIBLE
                            guess2CheckTextView.visibility = View.VISIBLE
                            guess2Entry.text = enteredText
                            guess2Result.text = colorfulResult
                            guess2Entry.visibility = View.VISIBLE
                            guess2Result.visibility = View.VISIBLE
                        }
                        else -> {
                            guess3TextView.visibility = View.VISIBLE
                            guess3CheckTextView.visibility = View.VISIBLE
                            guess3Entry.text = enteredText
                            guess3Result.text = colorfulResult
                            guess3Entry.visibility = View.VISIBLE
                            guess3Result.visibility = View.VISIBLE
                        }
                    }
                    if (result == "OOOO"){
                        // TODO show confetti here
                        confetti.start(Presets.explode())
                        wordle.text = actualWord
                        wordle.visibility = View.VISIBLE
//                        Toast.makeText(applicationContext, "VIOLA! You guessed it!", Toast.LENGTH_SHORT).show()
                        delayAndReset()
                    } else if (attempts == 3){
                        wordle.text = actualWord
                        wordle.visibility = View.VISIBLE
                        Toast.makeText(applicationContext, "Lets try another word", Toast.LENGTH_SHORT).show()
                        delayAndReset()
                    }
                }
            }
        }

        resetButton.setOnClickListener{
            reset()
        }
    }


    private fun checkNonAlphabetic(s: String): Boolean{
        return s.any{ !it.isLetter() }
    }

    private fun checkAnswer(answer: String, guess: String): String {
        val result = charArrayOf('*', '*', '*', '*') // * = unprocessed
        val dict = mutableMapOf<Char, Int>()
        for (ch in answer){
            dict[ch] = dict.getOrDefault(ch, 0) + 1
        }
        // in first pass, check for chars that match and put O in places they match
        for (i in guess.indices){
            val g = guess[i]
            if (answer[i] == g){
                result[i] = 'O'
                dict[g] = dict.getOrDefault(g, 0) - 1
                if (dict.getOrDefault(g, 0) == 0){
                    dict.remove(g)
                }
            }
        }
        // in second pass, check for the wrong positions
        for (i in guess.indices){
            if (result[i] != '*')
                continue
            val g = guess[i]
            if (dict.containsKey(g)){
                result[i] = '+'
                dict[g] = dict.getOrDefault(g, 0) - 1
                if (dict.getOrDefault(g, 0) == 0){
                    dict.remove(g)
                }
            }else{
                result[i] = 'X'
            }
        }
        return String(result)
    }


    private fun reset() = runBlocking{
        attempts = 0
        for (textView in allTextViews){
            textView?.visibility = View.INVISIBLE
        }
        userInput.setText("")
        actualWord = FourLetterWordList.getRandomFourLetterWord().uppercase()
        actualWord = "STAR"
    }


    private fun delayAndReset(){
        Handler().postDelayed({ reset() }, 2000L)
    }


    private fun getColorfulString(guess: String, result: String): SpannableStringBuilder{
        var spannableStringBuilder = SpannableStringBuilder()
        for (i in guess.indices){
            when (result[i]){
                'O' -> { spannableStringBuilder.append(guess[i].toString(),
                    ForegroundColorSpan(Color.GREEN), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                '+' -> { spannableStringBuilder.append(guess[i].toString(),
                    ForegroundColorSpan(Color.BLACK), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                'X' -> { spannableStringBuilder.append(guess[i].toString(),
                    ForegroundColorSpan(Color.RED), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        return spannableStringBuilder
    }


    private fun hideKeyboard(){
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = currentFocus
        inputMethodManager.hideSoftInputFromWindow(currentFocusView?.windowToken, 0)
    }

}