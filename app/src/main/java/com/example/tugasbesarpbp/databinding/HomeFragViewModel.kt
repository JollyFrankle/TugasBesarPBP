package com.example.tugasbesarpbp.databinding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasbesarpbp.room.MainDB
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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