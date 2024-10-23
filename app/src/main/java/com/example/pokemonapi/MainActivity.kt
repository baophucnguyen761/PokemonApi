package com.example.pokemonapi

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var pokeImageURL = ""
    var pokeName = ""
    var pokeType = ""
    var pokeID = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val randomButton = findViewById<Button>(R.id.randomButton)
        val imageView = findViewById<ImageView>(R.id.pokeImage)
        val pokeNameView = findViewById<TextView>(R.id.pokeName)
        val pokeTypeView = findViewById<TextView>(R.id.pokeType)

        // Get a random Pokémon when the button is clicked
        randomButton.setOnClickListener {
            pokeID = Random.nextInt(1, 399) // Random Pokémon ID (you can increase the range)
            pokemonInput(pokeID.toString(), imageView, pokeNameView, pokeTypeView)
        }
    }

    private fun pokemonInput(pokemonID: String, imageView: ImageView, pokeNameView: TextView, pokeTypeView: TextView) {
        val url = "https://pokeapi.co/api/v2/pokemon/$pokemonID"
        val client = AsyncHttpClient()
        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.d("Pokemon", "response successful: $json")
                val pokeJSON = json.jsonObject
                pokeImageURL = pokeJSON.getJSONObject("sprites").getString("front_default")
                pokeName = pokeJSON.getString("name").capitalize()
                pokeType = pokeJSON.getJSONArray("types").getJSONObject(0).getJSONObject("type").getString("name").capitalize()

                // Use Picasso to load image
                com.squareup.picasso.Picasso.get()
                    .load(pokeImageURL)
                    .fit()
                    .centerInside()
                    .into(imageView)

                pokeNameView.text = pokeName
                pokeTypeView.text = pokeType
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String, throwable: Throwable?) {
                Log.e("Pokemon Error", "Failed to fetch data: $response")
                pokeNameView.text = "Pokémon not found"
                pokeTypeView.text = ""
            }
        })
    }
}
