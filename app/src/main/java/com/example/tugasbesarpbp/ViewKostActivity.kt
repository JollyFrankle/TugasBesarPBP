package com.example.tugasbesarpbp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.tugasbesarpbp.databinding.ActivityViewKostBinding

class ViewKostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewKostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewKostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load("https://v3.himaforka-uajy.org/assets/images/logo.png")
            .into(binding.imgPlaceholder)
    }
}