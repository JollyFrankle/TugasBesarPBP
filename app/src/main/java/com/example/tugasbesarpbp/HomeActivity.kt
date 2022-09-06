package com.example.tugasbesarpbp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set title bar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Home - JogjaKost";

        // hide the action bar
        actionbar.hide()

    }
}