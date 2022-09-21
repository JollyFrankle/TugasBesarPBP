package com.example.tugasbesarpbp.entity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView

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
            ItemKost(1, "Kost Apik STAN Bintaro Andalusia Tipe C Pondok Aren Tangerang Selatan 8034AP", null, 1450000.0, fasilitasC, null, "https://static.mamikos.com/uploads/cache/data/style/2022-07-22/2JpISRi7-360x480.jpg", "Campur", -6.278, 106.739),
            ItemKost(2, "Kost Singgahsini Omah Anggur Tipe C Seturan Yogyakarta", null, 1720000.0, fasilitasC, null, "https://static.mamikos.com/uploads/cache/data/style/2021-11-03/kIeblytS-360x480.jpg", "Putri", -6.278, 106.739),
            ItemKost(3, "Kost Costin 1 Tipe A Depok Sleman", null, 775000.0, fasilitasC, null, "https://static.mamikos.com/uploads/cache/data/style/2022-06-06/1AMTr1Gi-360x480.jpg", "Putri", -6.278, 106.739),
            ItemKost(4, "Kost Hijau Wahid Hasyim Depok Sleman", null, 550000.0, fasilitasC, null, "https://static.mamikos.com/uploads/cache/data/style/2022-05-26/UBBY0CIb-360x480.jpg", "Putra", -6.278, 106.739),
            ItemKost(5, "Kost Dewi Jaya Depok Sleman", null, 800000.0, fasilitasC, null, "https://static.mamikos.com/uploads/cache/data/style/2022-06-06/dmut8kvh-360x480.jpg", "Putri", -6.278, 106.739),
            ItemKost(6, "Kost Palem Asri Tipe Vvip Depok Sleman", null, 1700000.0, fasilitasC, null, "https://static.mamikos.com/uploads/cache/data/style/2022-06-06/dAJrx2KO-360x480.jpg", "Putri", -6.278, 106.739),
            ItemKost(7, "Kost Buk Muslim Tipe A Depok Sleman", null, 1700000.0, fasilitasC, null, "https://static.mamikos.com/uploads/cache/data/style/2022-06-09/IrAahU10-360x480.jpg", "Putri", -6.278, 106.739),
            ItemKost(8, "Kost Khatarina Residence Depok Sleman", null, 470000.0, fasilitasC, null, "https://static.mamikos.com/uploads/cache/data/style/2021-12-22/0Qr7oBkx-360x480.jpg", "Putra", -6.278, 106.739),
            ItemKost(9, "Kost Pak Priyono Nologaten Depok Sleman Yogyakarta", null, 1100000.0, fasilitasC, null, "https://static.mamikos.com/uploads/cache/data/style/2022-08-12/qOz9vsap-360x480.jpg", "Putri", -6.278, 106.739),
            ItemKost(10, "Kost Bu Meidi Tipe B Depok Sleman", null, 600000.0, fasilitasC, null, "https://static.mamikos.com/uploads/cache/data/style/2020-06-24/luO4klVZ-360x480.jpg", "Putra", -6.278, 106.739),

            )
    }

    class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        init {
//            Toast.makeText(applicationContext, "Please wait, it may take a few minute...",     Toast.LENGTH_SHORT).show()
        }
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }

}