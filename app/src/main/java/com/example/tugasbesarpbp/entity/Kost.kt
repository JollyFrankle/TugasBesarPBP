package com.example.tugasbesarpbp.entity

class Kost(id: Int, nama: String, alamat: String, harga: Int, fasilitas: String?, deskripsi: String?, foto: String?, type: String, latitude: Double, longitude: Double) {
    var id: Int = 0
    var nama: String? = null
    var alamat: String? = null
    var harga: Int = 0
    var fasilitas: String? = null
    var deskripsi: String? = null
    var foto: String? = null
    var type: String? = null
    var latitude: Double = 0.toDouble()
    var longitude: Double = 0.toDouble()

    init {
        this.id = id
        this.nama = nama
        this.alamat = alamat
        this.harga = harga
        this.fasilitas = fasilitas
        this.deskripsi = deskripsi
        this.foto = foto
        this.type = type
        this.latitude = latitude
        this.longitude = longitude
    }

    // 1 data dummy
    companion object {
        var kosts = arrayOf(
            Kost(1, "Kost Putra SADEWA", "Jl. Berbaktimulyo No. 1", 1700000, "KIPAS SAJA HAHAHAHA", "Kost ini adalah\r\nKost terbaik di dunia", null, "Putra", -7.275, 112.791),
            Kost(2, "Kost Putri SADEWA", "Jl. Berbaktimulyo No. 1", 1700000, "KIPAS SAJA HAHAHAHA", "Kost ini adalah\r\nKost terbaik di dunia", null, "Putri", -7.275, 112.791),
            Kost(3, "Kost Campuran SADEWA", "Jl. Berbaktimulyo No. 1", 1700000, "KIPAS SAJA HAHAHAHA", "Kost ini adalah\r\nKost terbaik di dunia", null, "Campuran", -7.275, 112.791),
        )
    }
}