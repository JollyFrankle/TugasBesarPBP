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
            ItemKost(2, "Kost Singgahsini Omah Anggur Tipe C Seturan Yogyakarta", null, 1720000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Putri", -6.278, 106.739),
            ItemKost(3, "Kost Costin 1 Tipe A Depok Sleman", null, 775000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Putri", -6.278, 106.739),
            ItemKost(4, "Kost Hijau Wahid Hasyim Depok Sleman", null, 550000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Putra", -6.278, 106.739),
            ItemKost(5, "Kost Dewi Jaya Depok Sleman", null, 800000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Putri", -6.278, 106.739),
            ItemKost(6, "Kost Palem Asri Tipe Vvip Depok Sleman", null, 1700000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Putri", -6.278, 106.739),
            ItemKost(7, "Kost Buk Muslim Tipe A Depok Sleman", null, 1700000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Putri", -6.278, 106.739),
            ItemKost(8, "Kost Khatarina Residence Depok Sleman", null, 470000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Putra", -6.278, 106.739),
            ItemKost(9, "Kost Pak Priyono Nologaten Depok Sleman Yogyakarta", null, 1100000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Putri", -6.278, 106.739),
            ItemKost(10, "Kost Bu Meidi Tipe B Depok Sleman", null, 600000.0, fasilitasC, null, "https://lh3.googleusercontent.com/2dBw3e0xPpK37MzJ9pci2OySJiHhQCNY1RIHAYkJ5PBbBzApRNkbOgV0RCzsJw0VOOiiBxyoIc_QhbRxGiTw-DgHVc1-_NWaFJ0C=w1064-v0", "Putra", -6.278, 106.739),

            )
    }
}