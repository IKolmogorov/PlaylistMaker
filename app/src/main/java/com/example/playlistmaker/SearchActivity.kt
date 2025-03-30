package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    var searchedText: String = EMPTY_TEXT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val returnFrameLayout = findViewById<FrameLayout>(R.id.return_frame)
        val searchEditText = findViewById<EditText>(R.id.search_editText)
        val clearButton = findViewById<ImageView>(R.id.search_clear_imageView)
        val searchPlaceholderFrame = findViewById<FrameLayout>(R.id.search_placeholder)

        val itunesBaseUrl = "https://itunes.apple.com"

        val retrofit = Retrofit.Builder()
            .baseUrl(itunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val searchTrackService = retrofit.create(ItunesApi::class.java)

        val recycler = findViewById<RecyclerView>(R.id.trackList)

        recycler.layoutManager = LinearLayoutManager(this)

        clearButton.visibility = clearButtonVisibility(searchEditText.text)

        returnFrameLayout.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            displayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(displayIntent)
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
        }

        fun getTrackSearchResults(expression: String){
            searchTrackService
                .search(expression)
                .enqueue(object : Callback<SearchTrackResponse> {
                    override fun onResponse(
                        call: Call<SearchTrackResponse>,
                        response: Response<SearchTrackResponse>
                    ) {

                        val searchTrackResults = response.body()?.results

                        if (searchTrackResults != null) {
                            if (searchTrackResults.size > 0) {
                                searchPlaceholderFrame.visibility = View.GONE

                                val trackAdapter = TrackAdapter(searchTrackResults)
                                recycler.adapter = trackAdapter
                            }
                            else
                            {
                                val trackAdapter = TrackAdapter(ArrayList<Track>(0))
                                recycler.adapter = trackAdapter
                                searchPlaceholderFrame.visibility = View.VISIBLE
                            }
                        }
                        else
                        {
                            searchPlaceholderFrame.visibility = View.VISIBLE
                        }

                    }

                    override fun onFailure(call: Call<SearchTrackResponse>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
                })
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getTrackSearchResults(searchEditText.text.toString())
            }
            false
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                searchedText = s.toString()
            }
        }

        searchEditText.addTextChangedListener(searchTextWatcher)

    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCHED_TEXT_KEY, searchedText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchedText = savedInstanceState.getString(SEARCHED_TEXT_KEY, EMPTY_TEXT)
        val searchEditText = findViewById<EditText>(R.id.search_editText)
        searchEditText.setText(searchedText)
    }

    companion object {
        const val SEARCHED_TEXT_KEY = "SEARCHED_TEXT"
        const val EMPTY_TEXT = ""
    }
}