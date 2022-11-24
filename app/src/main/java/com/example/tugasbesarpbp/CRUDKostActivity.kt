package com.example.tugasbesarpbp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.example.tugasbesarpbp.api.http.KostApi
import com.example.tugasbesarpbp.room.kost.KostDao
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tugasbesarpbp.api.models.Kost
import com.example.tugasbesarpbp.databinding.ActivityCreateBinding
import com.google.gson.Gson
import timber.log.Timber

class CRUDKostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBinding

//    private var layoutLoading: LinearLayout? = null
    private var id: Long? = null
    private var action: Int = CREATE
//    private lateinit var kostDao: KostDao
    private var queue:RequestQueue? = null

    private lateinit var menu: Menu

    private lateinit var builderManager: NotificationManagerCompat
    private val CHANNEL_ID_1 = "channel_notification_01"
    private val notificationId1 = 101
    private val CHANNEL_ID_2 = "channel_notification_02"
    private val notificationId2 = 102
    private val CHANNEL_ID_3 = "channel_notification_03"
    private val notificationId3 = 103

    private var progressDone = 0

    private lateinit var token: String
    private var userId: Long = 0

    private val TIPE_KOST_LIST = arrayOf("Putra", "Putri", "Campur")

    companion object {
        const val CREATE = 1
        const val EDIT = 2
        const val READ = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        builderManager = NotificationManagerCompat.from(this)
        Timber.plant(Timber.DebugTree())

        createNotificationChannel()

        title = "Data Kost"

        // get token
        this.getSharedPreferences("session", Context.MODE_PRIVATE).let {
            token = it.getString("token", "")!!
            userId = it.getLong("id", 0)
        }

        // enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // get action from intent
        intent.let {
            action = it.getIntExtra("action", CREATE)
            id = it.getLongExtra("id", 0)
        }

        queue = Volley.newRequestQueue(this)

//        val tilNama = binding.tilTambahNama.editText?.text.toString()

//        val db by lazy { MainDB(this) }
//        kostDao = db.KostDao()

        // Set status dari input dan tombol2 yg bisa diklik berdasarkan CREATE, READ, atau UPDATE:
        this.setInputElements()

        binding.btnEdit.setOnClickListener {
            editData()
        }

        binding.btnDelete.setOnClickListener {
            deleteData()
        }

        binding.btnTambah.setOnClickListener {
            val nama = binding.tilTambahNama.editText?.text.toString()
            val fasilitas = binding.tilTambahFasilitas.editText?.text.toString()
            var harga = 0.0
            try {
                harga = binding.tilTambahHarga.editText?.text.toString().toDouble()
            } catch (e: Exception) {
                Log.d("ERROR", e.message.toString())
            }

            var error = 0

            if(nama.isEmpty()) {
                binding.tilTambahNama.error = "Nama tidak boleh kosong!"
                error++
            } else {
                binding.tilTambahNama.error = null
            }

            if(fasilitas.isEmpty()) {
                binding.tilTambahFasilitas.error = "Fasilitas tidak boleh kosong!"
                error++
            } else {
                binding.tilTambahFasilitas.error = null
            }

            if(harga <= 0.0) {
                binding.tilTambahHarga.error = "Harga tidak boleh kosong!"
                error++
            } else {
                binding.tilTambahHarga.error = null
            }

            if(error == 0){
                val kost = Kost(
                    namaKost = binding.tilTambahNama.editText?.text.toString(),
                    fasilitas = binding.tilTambahFasilitas.editText?.text.toString(),
                    harga = binding.tilTambahHarga.editText?.text.toString().toDouble(),
                    tipe = binding.actvTambahTipeKost.text.toString(),
                    idPemilik = null // ini nanti diisi dengan id pemilik yang login secara otomatis di backend
                )

                println(Gson().toJson(kost))

                setLoadingScreen(true)
                if(action == CREATE){
                    KostApi.createKost(this, kost, {
                        // Kalau masuk sini, sudah pasti berhasil
                        Toast.makeText(this, "Berhasil menambahkan kost baru.", Toast.LENGTH_SHORT).show()
                        finish()
                    }, {
                        setLoadingScreen(false)
                    })
                } else if(action == EDIT){
                    KostApi.updateKost(this, id!!, kost, {
                        // Kalau masuk sini, sudah pasti berhasil
                        Toast.makeText(this, "Berhasil mengubah data kost.", Toast.LENGTH_SHORT).show()
                        finish()
                    }, {
                        setLoadingScreen(false)
                    })
                }
            }
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
                editData()
            }
            R.id.action_delete -> {
                deleteData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // set dropdown
    private fun setExposedDropDownMenu() {
        val adapterTipe: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            R.layout.item_list, TIPE_KOST_LIST)
        binding.actvTambahTipeKost.setAdapter(adapterTipe)
    }

    // edit action
    private fun editData() {
        if(action == EDIT) {
            action = READ
        } else {
            action = EDIT
        }
        setInputElements()
    }

    // delete action
    private fun deleteData() {
        // confirm alert
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Konfirmasi")
        dialog.setMessage("Apakah anda yakin ingin menghapus data ini?")
        dialog.setPositiveButton("Ya") { _, _ ->
            setLoadingScreen(true)
            KostApi.deleteKost(this, id!!, {
                // Kalau masuk sini, sudah pasti berhasil
                Toast.makeText(this, "Berhasil menghapus data kost.", Toast.LENGTH_SHORT).show()
                finish()
            }, {
                setLoadingScreen(false)
            })
            setResult(RESULT_OK)
            finish()
        }
        dialog.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setInputElements() {
        if(action == EDIT || action == READ) {
            binding.btnTambah.text = "Update"

            // Load data
            setLoadingScreen(true)
            KostApi.getKostById(this, id!!, { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                var kost : Kost = gson.fromJson(
                    jsonObject.getJSONObject("data").toString(), Kost::class.java
                )

                binding.tilTambahNama.editText?.setText(kost.namaKost)
                binding.tilTambahHarga.editText?.setText(kost.harga.toString())
                binding.tilTambahFasilitas.editText?.setText(kost.fasilitas)
                binding.actvTambahTipeKost.setText(kost.tipe)
                setExposedDropDownMenu()

                if(kost.idPemilik != userId) {
                    action = READ
                    this.setEditDeleteBtn(false)
                } else {
                    this.setEditDeleteBtn(true)
                }

                // Takutnya ngespam ini vvv
//            Toast.makeText(this@CRUDKostActivity, "Data berhasil diambil!", Toast.LENGTH_SHORT).show()
                setLoadingScreen(false)
            })
        }

        if(action == CREATE) {
            // Send notification 3
            sendNotification3()

            setLoadingScreen(false)

            binding.btnEdit.isEnabled = false
            binding.btnDelete.isEnabled = false
        } else {
            binding.btnEdit.isEnabled = true
            binding.btnDelete.isEnabled = true
        }

        if(action == READ) {
            binding.tilTambahNama.isEnabled = false
            binding.tilTambahFasilitas.isEnabled = false
            binding.tilTambahHarga.isEnabled = false
            binding.tilTambahTipeKost.isEnabled = false
            binding.btnTambah.isEnabled = false

            // hide btnTambah
            binding.btnTambah.alpha = 0f
            binding.btnTambah.isClickable = false
        } else {
            binding.tilTambahNama.isEnabled = true
            binding.tilTambahFasilitas.isEnabled = true
            binding.tilTambahHarga.isEnabled = true
            binding.tilTambahTipeKost.isEnabled = true
            binding.btnTambah.isEnabled = true

            // show btnTambah
            binding.btnTambah.alpha = 1f
            binding.btnTambah.isClickable = true

            if(action == CREATE) {
                binding.btnTambah.text = "Create"
            }
        }
    }

    private fun setEditDeleteBtn(visibility: Boolean) {
//        if(action == CREATE) {
        menu.findItem(R.id.action_edit).isVisible = visibility
        menu.findItem(R.id.action_delete).isVisible = visibility
        binding.btnEdit.isVisible = visibility
        binding.btnDelete.isVisible = visibility
//        } else {
//            menu.findItem(R.id.action_edit).isVisible = true
//            menu.findItem(R.id.action_delete).isVisible = true
//        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descriptionText = "Notification Description"

            val channel1 = NotificationChannel(
                CHANNEL_ID_1,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }

    private fun sendNotification3() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setContentTitle("Create Kost")
            .setContentText("Creation in progress")
            .setSmallIcon(R.drawable.ic_home_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val PROGRESS_MAX = 100
        val PROGRESS_CURRENT = 0
        with(NotificationManagerCompat.from(this)) {
            builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, true)
            notify(notificationId3, builder.build())
            if(progressDone == 100){
                builder.setContentText("Kost created")
                    .setProgress(0, 0, false)
                notify(notificationId3, builder.build())
            }
        }
    }

    // DIBAWAH INI ADALAH CREATE UPDATE UNTUK KEPERLUAN API --> dipindahkan ke KostApi (file baru) untuk mempermudah pembacaan

    private fun getKostById(id: Long) {
        setLoadingScreen(true)
        val stringRequest: StringRequest = object : StringRequest(Method.GET, KostApi.GET_BY_ID_URL + id, Response.Listener { response ->
            val gson = Gson()
            val jsonObject = JSONObject(response)
            var kost : Kost = gson.fromJson(
                jsonObject.getJSONObject("data").toString(), Kost::class.java
            )

            binding.tilTambahNama.editText?.setText(kost.namaKost)
            binding.tilTambahHarga.editText?.setText(kost.harga.toString())
            binding.tilTambahFasilitas.editText?.setText(kost.fasilitas)
//                setExposedDropDownMenu()

            if(kost.idPemilik != userId) {
                action = READ
                this.setEditDeleteBtn(false)
            } else {
                this.setEditDeleteBtn(true)
            }

            // Takutnya ngespam ini vvv
//            Toast.makeText(this@CRUDKostActivity, "Data berhasil diambil!", Toast.LENGTH_SHORT).show()
            setLoadingScreen(false)
        }, Response.ErrorListener { error ->
            setLoadingScreen(false)

            try{
                val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                val errors = JSONObject(responseBody)
                Toast.makeText(
                    this@CRUDKostActivity,
                    errors.getString("message"),
                    Toast.LENGTH_SHORT
                ).show()
            } catch(e: Exception){
                Toast.makeText(this@CRUDKostActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String>{
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Authorization"] = "Bearer " + token
                return headers
            }
        }
        queue!!.add(stringRequest)
    }

    private fun createKost(){
        setLoadingScreen(true)
        val kost = Kost(
            namaKost = binding.tilTambahNama.editText?.text.toString(),
            fasilitas = binding.tilTambahFasilitas.editText?.text.toString(),
            harga = binding.tilTambahHarga.editText?.text.toString().toDouble(),
            tipe = "",
            idPemilik = null // ini nanti diisi dengan id pemilik yang login secara otomatis di backend
        )

        val stringRequest: StringRequest =
            object: StringRequest(Method.POST, KostApi.ADD_URL, Response.Listener { response ->
                val gson = Gson()
                var kost = gson.fromJson(response, Kost::class.java)

                if(kost != null)
                    Toast.makeText(this@CRUDKostActivity, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                Timber.tag("Add").d("Data Kost berhasil ditambahkan [!]")

                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()

                setLoadingScreen(true)
            }, Response.ErrorListener { error ->
                setLoadingScreen(false)

                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@CRUDKostActivity,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch(e: Exception){
                    Toast.makeText(this@CRUDKostActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>{
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer " + token
                    return headers
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val gson = Gson()
                    val requestBody = gson.toJson(kost)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String{
                    return "application/json"
                }
            }
        queue!!.add(stringRequest)
        allKost()
    }

    private fun updateKost(id: Long){
        setLoadingScreen(true)

        val kost = Kost(
            namaKost = binding.tilTambahNama.editText?.text.toString(),
            fasilitas = binding.tilTambahFasilitas.editText?.text.toString(),
            harga = binding.tilTambahHarga.editText?.text.toString().toDouble(),
            tipe = "",
            idPemilik = null // ini nanti diisi dengan id pemilik yang login secara otomatis di backend
        )

        val stringRequest: StringRequest =
            object: StringRequest(Method.PUT, KostApi.UPDATE_URL + id, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                var kost : Kost = gson.fromJson(
                    jsonObject.getJSONObject("data").toString(), Kost::class.java
                )

                if(kost != null)
                    Toast.makeText(this@CRUDKostActivity, "Data berhasil diubah", Toast.LENGTH_SHORT).show()
                Timber.tag("Update").d("Data Kost berhasil diubah [!]")

                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()

                setLoadingScreen(false)
            }, Response.ErrorListener { error ->
                setLoadingScreen(false)

                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@CRUDKostActivity,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch(e: Exception){
                    Toast.makeText(this@CRUDKostActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>{
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer " + token
                    return headers
                }

                @Throws(AuthFailureError::class)
                override fun getBody(): ByteArray {
                    val gson = Gson()
                    val requestBody = gson.toJson(kost)
                    return requestBody.toByteArray(StandardCharsets.UTF_8)
                }

                override fun getBodyContentType(): String{
                    return "application/json"
                }
            }
        queue!!.add(stringRequest)
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

    private fun allKost(){
        return
//        srMahasiswa!!.isRefreshing = true
        val stringRequest: StringRequest =
            object: StringRequest(Method.GET, KostApi.GET_ALL_URL, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                var kost : Array<Kost> = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(), Array<Kost>::class.java
                )

//                adapter!!.setMahasiswaList(mahasiswa)
//                adapter!!.filter.filter(svMahasiswa!!.query)
//                srMahasiswa!!.isRefreshing = false

                if(!kost.isEmpty())
                {
                    Toast.makeText(this@CRUDKostActivity, "Data berhasil diambil All Mahasiswa", Toast.LENGTH_SHORT).show()
                    Timber.tag("Show").d("Data Kost berhasil tertampil [!]")
                }
                else {
                    Toast.makeText(this@CRUDKostActivity, "Data kosong", Toast.LENGTH_SHORT).show()
                    Timber.tag("Error").e("Data Kost masih kosong [!]")
                }
            }, Response.ErrorListener { error ->
//                srMahasiswa!!.isRefreshing = false

                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@CRUDKostActivity,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch(e: Exception){
                    Toast.makeText(this@CRUDKostActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>{
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer " + token
                    return headers
                }
            }
        queue!!.add(stringRequest)
    }

    fun deleteKost(id: Long){
        setLoadingScreen(true)
        val stringRequest: StringRequest =
            object: StringRequest(Method.DELETE, KostApi.DELETE_URL + id , Response.Listener { response ->
                setLoadingScreen(false)

                val gson = Gson()
                var kost = gson.fromJson(response, Kost::class.java)

                if(kost != null)
                    Toast.makeText(this@CRUDKostActivity, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                Timber.d("Data Kost berhasil dihapus [!]")
                Timber.tag("Delete").d("Data Kost berhasil dihapus [!]")
                allKost()
            }, Response.ErrorListener { error ->
                setLoadingScreen(false)

                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        this@CRUDKostActivity,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch(e: Exception){
                    Toast.makeText(this@CRUDKostActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>{
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    headers["Authorization"] = "Bearer " + token
                    return headers
                }
            }
        queue!!.add(stringRequest)
    }

}