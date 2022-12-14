package com.example.tugasbesarpbp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.RequestQueue
import org.json.JSONObject
import com.android.volley.toolbox.Volley
import com.example.tugasbesarpbp.api.http.KostApi
import com.example.tugasbesarpbp.api.http.ReviewApi
import com.example.tugasbesarpbp.api.models.Kost
import com.example.tugasbesarpbp.api.models.Review
import com.example.tugasbesarpbp.databinding.ActivityCrudReviewBinding
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import timber.log.Timber

class CRUDReviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrudReviewBinding

    private var id_review: Long = 0
    private var id_kost: Long = 0
    private var action: Int = CREATE
    private var queue:RequestQueue? = null

    private lateinit var menu: Menu

    private lateinit var token: String
    private var id_user: Long = 0

    companion object {
        const val CREATE = 1
        const val EDIT = 2
//        const val READ = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrudReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())

        title = "Review Kost"

        // get token
        this.getSharedPreferences("session", Context.MODE_PRIVATE).let {
            token = it.getString("token", "")!!
            id_user = it.getLong("id", 0)
        }

        // enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // get action from intent
        intent.let {
            id_kost = it.getLongExtra("id_kost", 0)
        }

        if(id_kost == 0L) {
            FancyToast.makeText(this, "Kost tidak ditemukan", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
            finish()
            return
        }

        queue = Volley.newRequestQueue(this)

        // Set status dari input dan tombol2 yg bisa diklik berdasarkan CREATE, READ, atau UPDATE:
        this.setInputElements()

        binding.btnTambah.setOnClickListener {
//            var error = 0
//
//            if(error == 0){
                val review = Review(
                    id_kost = id_kost,
                    id_user = 0, // akan otomatis diisi di server
                    rating = binding.ratingBar.rating,
                    review = binding.tilCRUDReviewReview.editText?.text.toString()
                )

                // reset error
                binding.tilCRUDReviewReview.error = null

                println(Gson().toJson(review))

                setLoadingScreen(true)
                if(action == CREATE){
                    ReviewApi.createReview(this, id_kost, review, {
                        setLoadingScreen(false)
                        FancyToast.makeText(this, "Review berhasil ditambahkan", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                        finish()
                    }, {
                        setLoadingScreen(false)
                        FancyToast.makeText(this, "Review gagal ditambahkan", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                        val errors = it.jsonData.getJSONObject("errors")
                        if(errors.has("review")){
                            binding.tilCRUDReviewReview.error = errors.getJSONArray("review").getString(0)
                        }
                        if(errors.has("rating")){
                            FancyToast.makeText(this, errors.getJSONArray("rating").getString(0), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                        }
                    })
                } else if(action == EDIT){
                    ReviewApi.updateReview(this, id_review, review, {
                        setLoadingScreen(false)
                        FancyToast.makeText(this, "Review berhasil diubah", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                        finish()
                    }, {
                        setLoadingScreen(false)
                        FancyToast.makeText(this, "Review gagal diubah", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                        val errors = it.jsonData.getJSONObject("errors")
                        if(errors.has("review")){
                            binding.tilCRUDReviewReview.error = errors.getJSONArray("review").getString(0)
                        }
                        if(errors.has("rating")){
                            FancyToast.makeText(this, errors.getJSONArray("rating").getString(0), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                        }
                    })
                }
//            }
        }
    }

    // on navigation back button pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud_kost, menu)
        this.menu = menu!!
        this.setEditDeleteBtn(action == CREATE)
        return super.onCreateOptionsMenu(menu)
    }

    // options menu item selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_edit -> {
//                editData() // do nothing karena memang defaultnya edit
            }
            R.id.action_delete -> {
                deleteData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // delete action
    private fun deleteData() {
        // confirm alert
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Konfirmasi")
        dialog.setMessage("Apakah anda yakin ingin menghapus review ini?")
        dialog.setPositiveButton("Ya") { _, _ ->
            setLoadingScreen(true)
            ReviewApi.deleteReview(this, id_review, {
                setLoadingScreen(false)
                FancyToast.makeText(this, "Review berhasil dihapus", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                finish()
            }, {
                setLoadingScreen(false)
                FancyToast.makeText(this, "Review gagal dihapus", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
            })
            setResult(RESULT_OK)
            finish()
        }
        dialog.setNegativeButton("Tidak", null)
        dialog.show()
    }

    private fun setInputElements() {
        binding.btnTambah.text = "Memuat data..."

        // Load data
        setLoadingScreen(true)

        ReviewApi.getLoggedInUserReviewByKostId(this, id_kost, { response ->
            val gson = Gson()
            val jsonObject = JSONObject(response)
            // return object: { data: { review: {}, user: {} } }
            try {
                // coba ubah ke object
                val review : Review = gson.fromJson(
                    jsonObject.getJSONObject("data").getJSONObject("review").toString(), Review::class.java
                )
                action = EDIT

                binding.ratingBar.rating = review.rating
                binding.tilCRUDReviewReview.editText?.setText(review.review)
                id_review = review.id!!

                binding.btnTambah.text = "Ubah Review"

                this.setEditDeleteBtn(true)
            } catch (e: Exception) {
                // kalau tidak bisa diubah ke object, berarti data kosong
                action = CREATE

                id_review = 0

                binding.btnTambah.text = "Tambah Review"
                this.setEditDeleteBtn(false)
            } finally {
                // dapatkan data kost (sudah pasti ada kalau masuk sini)
                val kost : Kost = gson.fromJson(
                    jsonObject.getJSONObject("data").getJSONObject("kost").toString(), Kost::class.java
                )
                binding.tvNamaKost.text = kost.namaKost

                setLoadingScreen(false)
            }
        }, {
            // Kalau ada error
            FancyToast.makeText(this, it.jsonData.getString("message"), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
            finish()
        })

//        if(action == EDIT) {
//            binding.tilTambahNama.isEnabled = true
//            binding.tilTambahFasilitas.isEnabled = true
//            binding.tilTambahHarga.isEnabled = true
//            binding.tilTambahTipeKost.isEnabled = true
//            binding.btnTambah.isEnabled = true
//
//            // show btnTambah
//            binding.btnTambah.alpha = 1f
//            binding.btnTambah.isClickable = true
//
//            if(action == CREATE) {
//                binding.btnTambah.text = "Create"
//            }
//        }
    }

    private fun setEditDeleteBtn(visibility: Boolean) {
        menu.findItem(R.id.action_edit).isVisible = false
        menu.findItem(R.id.action_delete).isVisible = visibility
    }

    private fun setLoadingScreen(state: Boolean){
        val layoutLoader: ConstraintLayout = findViewById<View>(R.id.layoutLoader).findViewById(R.id.layoutLoader)
        if (state) {
            layoutLoader.visibility = View.VISIBLE
            layoutLoader.alpha = 1f

            // set flag to disable click
            layoutLoader.isClickable = true
        } else {
//            binding.layoutLoader.visibility = View.GONE
            // fade out
            layoutLoader.animate().alpha(0f).setDuration(250).withEndAction {
                layoutLoader.visibility = View.GONE
            }
            // set flag to enable click
            layoutLoader.isClickable = false
        }
    }
}