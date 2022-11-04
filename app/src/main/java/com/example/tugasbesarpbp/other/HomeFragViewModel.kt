package com.example.tugasbesarpbp.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeFragViewModel: ViewModel() {
    val currentWilayah: LiveData<String>
        get() = WilayahRepository.currentWilayah

    val textContent = MutableLiveData<String>()

    private val _displayedTextContent = MutableLiveData<String>()
    val displayedTextContent: LiveData<String>
        get() = _displayedTextContent

    fun changeWilayah() {
        textContent.value = WilayahRepository.getRandomWilayah()
        _displayedTextContent.value = textContent.value
    }
}