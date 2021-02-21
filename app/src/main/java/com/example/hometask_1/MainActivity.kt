package com.example.hometask_1

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wolfram.alpha.WAEngine
import com.wolfram.alpha.WAPlainText

class MainActivity : AppCompatActivity() {

    val key = "V59LWX-QU35LRR4J3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.topAppBar))

        val editText = findViewById<EditText>(R.id.question_input)
        val button = findViewById<Button>(R.id.search_button)

        button.setOnClickListener {
            val text = editText.text.toString()
            askWolfram(text)
        }
    }

    fun askWolfram(question: String) {
        val engine = WAEngine()
        engine.appID = key
        engine.addFormat("plaintext")

        val query = engine.createQuery()
        query.input = question
        var answer: String? = null

        Thread(Runnable {
            val queryResult = engine.performQuery(query)
            val answerText = findViewById<TextView>(R.id.answer_output)

            if (queryResult.isError) {
                Log.e("wolfram error", queryResult.errorMessage)
                answer = queryResult.errorMessage
            } else if (!queryResult.isSuccess) {
                Log.e("wolfram error", "Sorry, I don't understand, can you rephrase?")
                answer = "Sorry, I don't understand, can you rephrase?"
            } else {
                for (pod in queryResult.pods) {
                    if (!pod.isError) {
                        for (subpod in pod.subpods) {
                            for (element in subpod.contents) {
                                if (element is WAPlainText) {
                                    Log.d("wolfram", element.text)
                                    answer = element.text
                                }
                            }
                        }
                    }
                }
            }
            answerText?.post { // Возвращаемся на Main Thread
                answerText?.text = answer
            }
        }).start()
    }
}