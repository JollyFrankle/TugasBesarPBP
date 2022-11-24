package com.example.tugasbesarpbp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.example.tugasbesarpbp.api.KostApi
import com.example.tugasbesarpbp.room.MainDB
import com.example.tugasbesarpbp.room.kost.KostDao
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tugasbesarpbp.api_models.Kost
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
import timber.log.Timber

class CRUDKostActivity : AppCompatActivity() {

//    private var binding: ActivityCreateBinding? = null

    private lateinit var btnTambah: Button
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var tilTambahNama: TextInputLayout
    private lateinit var tilTambahFasilitas: TextInputLayout
    private lateinit var tilTambahHarga: TextInputLayout

//    private var layoutLoading: LinearLayout? = null
    private var id: Long? = null
    private var action: Int = CREATE
    private lateinit var kostDao: KostDao
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

    companion object {
        const val CREATE = 1
        const val EDIT = 2
        const val READ = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

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
        btnTambah = findViewById(R.id.btnTambah)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)
        tilTambahNama = findViewById(R.id.tilTambahNama)
        tilTambahFasilitas = findViewById(R.id.tilTambahFasilitas)
        tilTambahHarga = findViewById(R.id.tilTambahHarga)
//        val tilNama = binding.tilTambahNama.editText?.text.toString()

//        val db by lazy { MainDB(this) }
//        kostDao = db.KostDao()

        // Set status dari input dan tombol2 yg bisa diklik berdasarkan CREATE, READ, atau UPDATE:
        this.setInputElements()

        btnEdit.setOnClickListener {
            editData()
        }

        btnDelete.setOnClickListener {
            deleteData()
        }

        btnTambah.setOnClickListener {
            val nama = tilTambahNama.editText?.text.toString()
            val fasilitas = tilTambahFasilitas.editText?.text.toString()
            var harga = 0.0
            try {
                harga = tilTambahHarga.editText?.text.toString().toDouble()
            } catch (e: Exception) {
                Timber.tag("ERROR").d(e.message.toString())
            }

            var error = 0

            if(nama.isEmpty()) {
                tilTambahNama.error = "Nama tidak boleh kosong!"
                error++
            } else {
                tilTambahNama.error = null
            }

            if(fasilitas.isEmpty()) {
                tilTambahFasilitas.error = "Fasilitas tidak boleh kosong!"
                error++
            } else {
                tilTambahFasilitas.error = null
            }

            if(harga <= 0.0) {
                tilTambahHarga.error = "Harga tidak boleh kosong!"
                error++
            } else {
                tilTambahHarga.error = null
            }

            if(error == 0){
                if(action == CREATE){
                    createKost()
                } else if(action == EDIT){
                    updateKost(id!!)
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
        dialog.setPositiveButton("Ya") { dialog, which ->
            deleteKost(id!!)
            setResult(RESULT_OK)
            finish()
        }
        dialog.setNegativeButton("Tidak") { dialog, which ->
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setInputElements() {
        if(action == EDIT || action == READ) {
            btnTambah.text = "Update"
            getKostById(id!!)
        }

        if(action == CREATE) {
            // Send notification 3
            sendNotification3()

            btnEdit.isEnabled = false
            btnDelete.isEnabled = false
        } else {
            btnEdit.isEnabled = true
            btnDelete.isEnabled = true
        }

        if(action == READ) {
            tilTambahNama.isEnabled = false
            tilTambahFasilitas.isEnabled = false
            tilTambahHarga.isEnabled = false
            btnTambah.isEnabled = false

            // hide btnTambah
            btnTambah.alpha = 0f
            btnTambah.isClickable = false
        } else {
            tilTambahNama.isEnabled = true
            tilTambahFasilitas.isEnabled = true
            tilTambahHarga.isEnabled = true
            btnTambah.isEnabled = true

            // show btnTambah
            btnTambah.alpha = 1f
            btnTambah.isClickable = true

            if(action == CREATE) {
                btnTambah.text = "Create"
            }
        }
    }

    private fun setEditDeleteBtn(visibility: Boolean) {
//        if(action == CREATE) {
        menu.findItem(R.id.action_edit).isVisible = visibility
        menu.findItem(R.id.action_delete).isVisible = visibility
        btnEdit.isVisible = visibility
        btnDelete.isVisible = visibility
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

    // DIBAWAH INI ADALAH CREATE UPDATE UNTUK KEPERLUAN API

    private fun getKostById(id: Long) {
        setLoading(true)
        val stringRequest: StringRequest = object : StringRequest(Method.GET, KostApi.GET_BY_ID_URL + id, Response.Listener { response ->
            val gson = Gson()
            val jsonObject = JSONObject(response)
            var kost : Kost = gson.fromJson(
                jsonObject.getJSONObject("data").toString(), Kost::class.java
            )

            tilTambahNama.editText?.setText(kost.namaKost)
            tilTambahHarga.editText?.setText(kost.harga.toString())
            tilTambahFasilitas.editText?.setText(kost.fasilitas)
//                setExposedDropDownMenu()

            if(kost.idPemilik != userId) {
                action = READ
                this.setEditDeleteBtn(false)
            } else {
                this.setEditDeleteBtn(true)
            }

            // Takutnya ngespam ini vvv
//            Toast.makeText(this@CRUDKostActivity, "Data berhasil diambil!", Toast.LENGTH_SHORT).show()
            setLoading(false)
        }, Response.ErrorListener { error ->
            setLoading(false)

            try{
                val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                val errors = JSONObject(responseBody)
                FancyToast.makeText(this@CRUDKostActivity, errors.getString("message"), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
//                Toast.makeText(
//                    this@CRUDKostActivity,
//                    errors.getString("message"),
//                    Toast.LENGTH_SHORT
//                ).show()
            } catch(e: Exception){
//                Toast.makeText(this@CRUDKostActivity, e.message, Toast.LENGTH_SHORT).show()
                FancyToast.makeText(this@CRUDKostActivity, e.message, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
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
        setLoading(true)
        val kost = Kost(
            namaKost = tilTambahNama.editText?.text.toString(),
            fasilitas = tilTambahFasilitas.editText?.text.toString(),
            harga = tilTambahHarga.editText?.text.toString().toDouble(),
            idPemilik = null // ini nanti diisi dengan id pemilik yang login secara otomatis di backend
        )

        val stringRequest: StringRequest =
            object: StringRequest(Method.POST, KostApi.ADD_URL, Response.Listener { response ->
                val gson = Gson()
                var kost = gson.fromJson(response, Kost::class.java)

                if(kost != null){
//                    Toast.makeText(this@CRUDKostActivity, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    FancyToast.makeText(this@CRUDKostActivity, "Data berhasil ditambahkan", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                    Timber.tag("Add").d("Data Kost berhasil ditambahkan [!]")
                }

                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()

                setLoading(true)
            }, Response.ErrorListener { error ->
                setLoading(false)

                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    FancyToast.makeText(this@CRUDKostActivity, errors.getString("message"), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
//                    Toast.makeText(
//                        this@CRUDKostActivity,
//                        errors.getString("message"),
//                        Toast.LENGTH_SHORT
//                    ).show()
                } catch(e: Exception){
                    FancyToast.makeText(this@CRUDKostActivity, e.message, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
//                    Toast.makeText(this@CRUDKostActivity, e.message, Toast.LENGTH_SHORT).show()
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
        setLoading(true)

        val kost = Kost(
            namaKost = tilTambahNama.editText?.text.toString(),
            fasilitas = tilTambahFasilitas.editText?.text.toString(),
            harga = tilTambahHarga.editText?.text.toString().toDouble(),
            idPemilik = null // ini nanti diisi dengan id pemilik yang login secara otomatis di backend
        )

        val stringRequest: StringRequest =
            object: StringRequest(Method.PUT, KostApi.UPDATE_URL + id, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                var kost : Kost = gson.fromJson(
                    jsonObject.getJSONObject("data").toString(), Kost::class.java
                )

                if(kost != null){
                    FancyToast.makeText(this@CRUDKostActivity, "Data berhasil diubah", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                    Timber.tag("Update").d("Data Kost berhasil diubah [!]")
                }

                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()

                setLoading(false)
            }, Response.ErrorListener { error ->
                setLoading(false)

                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    FancyToast.makeText(
                        this@CRUDKostActivity,
                        errors.getString("message"),
                        FancyToast.LENGTH_SHORT,
                        FancyToast.ERROR,
                        false
                    ).show()
                } catch(e: Exception){
                    FancyToast.makeText(this@CRUDKostActivity, e.message, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
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

    private fun setLoading(isLoading: Boolean){
        if(isLoading){
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
//            layoutLoading!!.visibility = View.VISIBLE
        } else{
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//            layoutLoading!!.visibility = View.INVISIBLE
        }
    }

    private fun allKost(){
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
                    FancyToast.makeText(this@CRUDKostActivity, "Data berhasil diambil All Mahasiswa", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                    Timber.tag("Show").d("Data Kost berhasil tertampil [!]")
                }
                else {
                    FancyToast.makeText(this@CRUDKostActivity, "Data kosong", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
                    Timber.tag("Error").e("Data Kost masih kosong [!]")
                }
            }, Response.ErrorListener { error ->
//                srMahasiswa!!.isRefreshing = false

                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    FancyToast.makeText(
                        this@CRUDKostActivity,
                        errors.getString("message"),
                        FancyToast.LENGTH_SHORT,
                        FancyToast.ERROR,
                        false
                    ).show()
                } catch(e: Exception){
                    FancyToast.makeText(this@CRUDKostActivity, e.message, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
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
        setLoading(true)
        val stringRequest: StringRequest =
            object: StringRequest(Method.DELETE, KostApi.DELETE_URL + id , Response.Listener { response ->
                setLoading(false)

                val gson = Gson()
                var kost = gson.fromJson(response, Kost::class.java)

                if(kost != null){
                    FancyToast.makeText(this@CRUDKostActivity, "Data berhasil dihapus", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                    Timber.tag("Delete").d("Data Kost berhasil dihapus [!]")
                }
                allKost()
            }, Response.ErrorListener { error ->
                setLoading(false)

                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    FancyToast.makeText(
                        this@CRUDKostActivity,
                        errors.getString("message"),
                        FancyToast.LENGTH_SHORT,
                        FancyToast.ERROR,
                        false
                    ).show()
                } catch(e: Exception){
                    FancyToast.makeText(this@CRUDKostActivity, e.message, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
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