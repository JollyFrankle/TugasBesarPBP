package com.example.tugasbesarpbp.other

import androidx.lifecycle.MutableLiveData

object WilayahRepository {
    private val wilayahJogja: List<String> = listOf(
        // Kabupaten dan Kota
        "Bantul",
        "Gunung Kidul",
        "Kulon Progo",
        "Sleman",
        "Yogyakarta",
        // Kecamatan di Bantul
        "Bambanglipuro",
        "Banguntapan",
        "Bantul",
        "Dlingo",
        "Imogiri",
        "Jetis",
        "Kasihan",
        "Kretek",
        "Pajangan",
        "Pandak",
        "Piyungan",
        "Pleret",
        "Pundong",
        "Sanden",
        "Sedayu",
        "Sewon",
        "Srandakan",
        // Kecamatan di Gunung Kidul
        "Gedangsari",
        "Girisubo",
        "Karangmojo",
        "Ngawen",
        "Nglipar",
        "Paliyan",
        "Panggang",
        "Patuk",
        "Playen",
        "Ponjong",
        "Purwosari",
        "Rongkop",
        "Saptosari",
        "Semanu",
        "Semin",
        "Tanjungsari",
        "Tepus",
        "Wonosari",
        // Kecamatan di Kulon Progo
        "Galur",
        "Girimulyo",
        "Kalibawang",
        "Kokap",
        "Lendah",
        "Nanggulan",
        "Panjatan",
        "Pengasih",
        "Samigaluh",
        "Sentolo",
        "Temon",
        "Wates",
        // Kecamatan di Sleman
        "Berbah",
        "Cangkringan",
        "Depok",
        "Gamping",
        "Godean",
        "Kalasan",
        "Minggir",
        "Mlati",
        "Moyudan",
        "Ngaglik",
        "Ngemplak",
        "Pakem",
        "Prambanan",
        "Seyegan",
        "Sleman",
        "Tempel",
        "Turi",
        // Kecamatan di Yogyakarta
        "Danurejan",
        "Gedong Tengen",
        "Gondokusuman",
        "Gondomanan",
        "Jetis",
        "Kotagede",
        "Kraton",
        "Mantrijeron",
        "Mergangsan",
        "Ngampilan",
        "Pakualaman",
        "Tegalrejo",
        "Umbulharjo",
        "Wirobrajan"
    )

    private val _currentWilayah = MutableLiveData<String>()
    val currentWilayah: MutableLiveData<String>
        get() = _currentWilayah

    init {
        _currentWilayah.value = wilayahJogja[0]
    }

    fun getRandomWilayah(): String {
        return wilayahJogja.random()
    }

    fun changeCurrentWilayah() {
        _currentWilayah.value = getRandomWilayah()
    }
}