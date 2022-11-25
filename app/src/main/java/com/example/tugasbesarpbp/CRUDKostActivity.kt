package com.example.tugasbesarpbp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import com.android.volley.RequestQueue
import com.example.tugasbesarpbp.api.http.KostApi
import org.json.JSONObject
import com.android.volley.toolbox.Volley
import com.example.tugasbesarpbp.api.models.Kost
import com.example.tugasbesarpbp.databinding.ActivityCreateBinding
import com.google.gson.Gson
import com.shashank.sony.fancytoastlib.FancyToast
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
                Timber.tag("Error").d(e.message.toString())
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
                    alamat = null,
                    idPemilik = null // ini nanti diisi dengan id pemilik yang login secara otomatis di backend
                )

                println(Gson().toJson(kost))

                setLoadingScreen(true)
                if(action == CREATE){
                    KostApi.createKost(this, kost, {
                        // Kalau masuk sini, sudah pasti berhasil
                        FancyToast.makeText(this, "Berhasil menambahkan kost baru.", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                        Timber.tag("Add").d("Data Kost berhasil ditambahkan [!]")
                        finish()
                    }, {
                        setLoadingScreen(false)
                    })
                } else if(action == EDIT){
                    KostApi.updateKost(this, id!!, kost, {
                        // Kalau masuk sini, sudah pasti berhasil
                        FancyToast.makeText(this, "Berhasil mengubah data kost.", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                        Timber.tag("Update").d("Data Kost berhasil diubah [!]")
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
                FancyToast.makeText(this, "Berhasil menghapus data kost.", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                Timber.tag("Delete").d("Data Kost berhasil dihapus [!]")
                finish()
            }, {
                FancyToast.makeText(this, "Gagal menghapus data kost.", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
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
            }, {
                FancyToast.makeText(this, "Gagal mengambil data kost.", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                // langsung saja finish activity
                finish()
            })
        }

        if(action == CREATE) {
            // Send notification 3
            sendNotification3()

            setExposedDropDownMenu()

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
        menu.findItem(R.id.action_edit).isVisible = visibility
        menu.findItem(R.id.action_delete).isVisible = visibility
        binding.btnEdit.isVisible = visibility
        binding.btnDelete.isVisible = visibility
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

    // DIBAWAH INI ADALAH CREATE UPDATE UNTUK KEPERLUAN API --> sudah dipindahkan ke \api\http\KostApi (file baru) untuk mempermudah pembacaan
    // end of file
}