package com.example.unit1project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private var attempts = 0
    private lateinit var allTextViews: Array<TextView?>
    private lateinit var actualWord: String
    private lateinit var userInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            val enteredText = userInput.text.toString().uppercase()
            if (enteredText == ""){
                Toast.makeText(applicationContext, "Enter something first", Toast.LENGTH_SHORT).show()
            } else{
                if (enteredText.length > 4){
                    Toast.makeText(applicationContext, "Enter only 4 characters", Toast.LENGTH_SHORT).show()
                } else if (checkNonAlphabetic(enteredText)){
                    Toast.makeText(applicationContext, "Enter characters ONLY", Toast.LENGTH_SHORT).show()
                } else{
                    attempts ++
                    val result = checkAnswer(actualWord, enteredText)
//                    Toast.makeText(applicationContext, result, Toast.LENGTH_LONG).show()

                    when (attempts) {
                        1 -> { // show guess 1 and its result
                            guess1TextView.visibility = View.VISIBLE
                            guess1CheckTextView.visibility = View.VISIBLE
                            guess1Entry.text = enteredText
                            guess1Result.text = result
                            guess1Entry.visibility = View.VISIBLE
                            guess1Result.visibility = View.VISIBLE
                        }
                        2 -> {
                            guess2TextView.visibility = View.VISIBLE
                            guess2CheckTextView.visibility = View.VISIBLE
                            guess2Entry.text = enteredText
                            guess2Result.text = result
                            guess2Entry.visibility = View.VISIBLE
                            guess2Result.visibility = View.VISIBLE
                        }
                        3 -> {
                            guess3TextView.visibility = View.VISIBLE
                            guess3CheckTextView.visibility = View.VISIBLE
                            guess3Entry.text = enteredText
                            guess3Result.text = result
                            guess3Entry.visibility = View.VISIBLE
                            guess3Result.visibility = View.VISIBLE
                        }
                    }
                    if (result == "OOOO"){
                        // TODO show confetti here
                        Toast.makeText(applicationContext, "VIOLA! You guessed it!", Toast.LENGTH_SHORT).show()
                        reset()
                    }

                    if (attempts == 3){
                        reset()
                        wordle.text = actualWord
                        Toast.makeText(applicationContext, "Lets try another word", Toast.LENGTH_SHORT).show()
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


    private fun reset(){
        attempts = 0
        for (textView in allTextViews){
            textView?.visibility = View.INVISIBLE
        }
        userInput.setText("")
        actualWord = FourLetterWordList.getRandomFourLetterWord().uppercase()
        actualWord = "STAR"

        runBlocking {
            launch {
                delay(2000)
            }
        }
    }
}