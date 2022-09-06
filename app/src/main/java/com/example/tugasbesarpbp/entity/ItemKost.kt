package com.example.tugasbesarpbp.entity

class ItemKost(id: Int, nama: String, alamat: String?, harga: Double, fasilitas: Array<String>, deskripsi: String?, foto: String?, type: String, latitude: Double, longitude: Double) {
    var id: Int = 0
    var nama: String? = null
    var alamat: String? = null
    var harga: Double = 0.0
    var fasilitas: Array<String>? = null
    var deskripsi: String? = null
    var foto: String? = null
    var tipe: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    init {
        this.id = id
        this.nama = nama
        this.alamat = alamat
        this.harga = harga
        this.fasilitas = fasilitas
        this.deskripsi = deskripsi
        this.foto = foto
        this.tipe = type
        this.latitude = latitude
        this.longitude = longitude
    }

    // 1 data dummy
    companion object {
        val fasilitasC = arrayOf(
            "Kamar Mandi Dalam",
            "AC",
            "Kasur",
            "Kipas Angin",
            "Meja Belajar",
            "Lemari",
            "Kamar Mandi Luar"
        )
        var itemKosts = arrayOf(
            ItemKost(1, "Kost Apik STAN Bintaro Andalusia Tipe C Pondok Aren Tangerang Selatan 8034AP", null, 1450000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Campur", -6.278, 106.739),
            ItemKost(2, "Kost Singgahsini Omah Anggur Tipe C Seturan Yogyakarta", null, 1450000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Campur", -6.278, 106.739),
            ItemKost(3, "Kost Singgahsini Omah Anggur Tipe C Seturan Yogyakarta", null, 1450000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Campur", -6.278, 106.739),
            ItemKost(4, "Kost Singgahsini Omah Anggur Tipe C Seturan Yogyakarta", null, 1450000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Campur", -6.278, 106.739),
            ItemKost(5, "Kost Singgahsini Omah Anggur Tipe C Seturan Yogyakarta", null, 1450000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Campur", -6.278, 106.739),
            ItemKost(6, "Kost Singgahsini Omah Anggur Tipe C Seturan Yogyakarta", null, 1450000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Campur", -6.278, 106.739),
            ItemKost(7, "Kost Singgahsini Omah Anggur Tipe C Seturan Yogyakarta", null, 1450000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Campur", -6.278, 106.739),
            ItemKost(8, "Kost Singgahsini Omah Anggur Tipe C Seturan Yogyakarta", null, 1450000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Campur", -6.278, 106.739),
            ItemKost(9, "Kost Singgahsini Omah Anggur Tipe C Seturan Yogyakarta", null, 1450000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Campur", -6.278, 106.739),
            ItemKost(10, "Kost Singgahsini Omah Anggur Tipe C Seturan Yogyakarta", null, 1450000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Campur", -6.278, 106.739),

            )
    }
}