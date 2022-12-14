package com.example.tugasbesarpbp

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tugasbesarpbp.api.http.KostApi
import com.example.tugasbesarpbp.api.http.ReviewApi
import com.example.tugasbesarpbp.api.models.Kost
import com.example.tugasbesarpbp.api.models.Review
import com.example.tugasbesarpbp.api.models.User
import com.example.tugasbesarpbp.databinding.ActivityViewKostBinding
import com.example.tugasbesarpbp.rv_adapters.RVReviewAdapter
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.json.JSONObject
import java.util.*


class ViewKostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewKostBinding
    private val KOST_IMAGES = arrayOf(
        "https://blog-media.lifepal.co.id/app/uploads/sites/3/2018/07/25140505/kost-eksklusif.jpg",
        "https://cdn-2.tstatic.net/bogor/foto/bank/images/info-kosan.jpg",
        "https://ima-prm-buck.s3.ap-southeast-1.amazonaws.com/article/medium/article_1609775497.jpg",
        "https://infokost.id/wp-content/uploads/2022/02/3523726IMG_24026209f83532e56-1-scaled.jpg",
        "https://www.sewakost.com/files/09-2021/ad63156/kost-kos-putra-uii-1462761661_large.jpg",
        "https://pix10.agoda.net/hotelImages/26042022/-1/ac986b75a443a7c542ddf621ed19d499.jpg?ca=27&ce=0&s=1024x768",
        "https://d3p0bla3numw14.cloudfront.net/news-content/img/2020/09/03133703/kost-eksklusif-atau-elit.jpg"
    )
    private lateinit var loader: ConstraintLayout
    private var id: Long? = null
    private lateinit var rvReviewAdapter: RVReviewAdapter
    private lateinit var rvReview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewKostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get id from intent
        id = intent.extras?.getLong("id")

        if(id == null) {
            // finish activity
            finish()
            return
        }

        // loader
        loader = binding.layoutLoader.layoutLoader

        setupRVReview()
    }

    private fun loadData() {
        setLoadingScreen(true)
        KostApi.getKostById(this, id!!, {
            Glide.with(this)
                .load(KOST_IMAGES.random())
                .placeholder(R.drawable.ic_lock_lock)
                .dontAnimate()
                .into(binding.imgPlaceholder)

            val gson = Gson()
            val jsonObj = JSONObject(it).getJSONObject("data")
            val kost = gson.fromJson(jsonObj.toString(), Kost::class.java)
            val user = gson.fromJson(jsonObj.getJSONObject("users").toString(), User::class.java)

            val reviews_count = jsonObj.getInt("reviews_count")
            var reviews_avg_rating = 0.0
            if(reviews_count > 0) {
                reviews_avg_rating = jsonObj.getDouble("reviews_avg_rating")
            }

            binding.tvAlamatKost.text = "Alamat kost entah di mana"
            binding.tvFasilitas.text = kost.fasilitas
            binding.tvTipeKost.text = "Kost ${kost.tipe}"
            binding.tvNamaKost.text = kost.namaKost
            binding.tvNamaPengelola.text = user.nama
            binding.tvKecamatan.text = "Entah dimana"
            binding.tvPrice.text = "Rp " + "%,.0f".format(Locale("id", "ID"), kost.harga)

            // QR Code Kost
            val qrCodeData = "app.jogjakost.qr.kost\r\n${kost.id}\r\nNama: ${kost.namaKost}"

            // QR Code
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(qrCodeData, BarcodeFormat.QR_CODE, 400, 400)
            val imageViewQrCode = binding.imgQRKost
            imageViewQrCode.setImageBitmap(bitmap)

            setCardButton(jsonObj.getBoolean("isPemilik"), kost.id!!)

            if(reviews_count > 0) {
                binding.tvRatingCount.text = "${reviews_count} ulasan"
                binding.tvAvgRating.text = String.format("%.1f", reviews_avg_rating, Locale("id", "ID"))
            } else {
                binding.tvRatingCount.text = "Belum ada ulasan"
                binding.tvAvgRating.text = "?"
            }
            setLoadingScreen(false)

            // load review
            loadReviews()
        }, {
            finish()
        })
    }

    private fun setCardButton(isPemilik: Boolean, id: Long) {
        if(isPemilik) {
            binding.btnBottomCard.text = "Edit/Hapus"
            binding.btnBottomCard.setOnClickListener {
                val intent = Intent(this, CRUDKostActivity::class.java)
                intent.putExtra("id", id)
                intent.putExtra("action", CRUDKostActivity.EDIT)
                startActivity(intent)
            }
        } else {
            binding.btnBottomCard.text = "Pesan"
        }
    }

    private fun setLoadingScreen(state: Boolean){
        if (state) {
            // fade in
            loader.alpha = 0f
            loader.visibility = View.VISIBLE
            loader.animate().alpha(1f).duration = 250

            // set flag to disable click
            loader.isClickable = true
        } else {
            // fade out
            loader.animate().alpha(0f).setDuration(250).withEndAction {
                loader.visibility = View.GONE
            }
            // set flag to enable click
            loader.isClickable = false
        }
    }

    private fun setupRVReview() {
        rvReview = binding.rvReview
        rvReviewAdapter = RVReviewAdapter(arrayListOf(), object: RVReviewAdapter.OnAdapterListener {
            override fun onClick(item: Review) {
                // do nothing
            }
        })
        rvReview.apply {
            layoutManager = LinearLayoutManager(this@ViewKostActivity)
            adapter = rvReviewAdapter
        }

        // btnCRUDReview
        binding.btnCRUDReview.setOnClickListener {
            val intent = Intent(this, CRUDReviewActivity::class.java)
            intent.putExtra("id_kost", id) // <-- id kost, nanti akan dicari di backend apakah ketemu review dari user yg sedang login untuk kost ini atau tidak
            startActivity(intent)
        }
    }

    private fun loadReviews() {
        ReviewApi.getAllByIdKost(this, id!!, { response->
            val gson = Gson()
            val jsonObject = JSONObject(response)
            val kost = gson.fromJson(
                jsonObject.getJSONArray("data").toString(), Array<Review>::class.java
            )
            rvReviewAdapter.setData(kost.toCollection(ArrayList()))
        }, {
            // do nothing
        })
    }

    override fun onStart() {
        super.onStart()

        loadData()
    }
}