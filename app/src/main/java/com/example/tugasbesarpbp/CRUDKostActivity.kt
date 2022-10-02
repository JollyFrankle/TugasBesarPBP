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
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tugasbesarpbp.room.MainDB
import com.example.tugasbesarpbp.room.kost.Kost
import com.example.tugasbesarpbp.room.kost.KostDao
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CRUDKostActivity : AppCompatActivity() {

//    private var binding: ActivityCreateBinding? = null

    private lateinit var btnTambah: Button
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var tilTambahNama: TextInputLayout
    private lateinit var tilTambahFasilitas: TextInputLayout
    private lateinit var tilTambahHarga: TextInputLayout
    private var id: Int = 0
    private var action: Int = CREATE
    private lateinit var kostDao: KostDao

    private lateinit var menu: Menu

    private lateinit var builderManager: NotificationManagerCompat
    private val CHANNEL_ID_1 = "channel_notification_01"
    private val notificationId1 = 101
    private val CHANNEL_ID_2 = "channel_notification_02"
    private val notificationId2 = 102
    private val CHANNEL_ID_3 = "channel_notification_03"
    private val notificationId3 = 103

    private var progressDone = 0

    companion object {
        const val CREATE = 1
        const val EDIT = 2
        const val READ = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        builderManager = NotificationManagerCompat.from(this)

        createNotificationChannel()

        title = "Data Kost"

        // enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // get action from intent
        action = intent.getIntExtra("action", CREATE)
        id = intent.getIntExtra("id", 0)

        btnTambah = findViewById(R.id.btnTambah)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)
        tilTambahNama = findViewById(R.id.tilTambahNama)
        tilTambahFasilitas = findViewById(R.id.tilTambahFasilitas)
        tilTambahHarga = findViewById(R.id.tilTambahHarga)
//        val tilNama = binding.tilTambahNama.editText?.text.toString()

        val db by lazy { MainDB(this) }
        kostDao = db.KostDao()

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
                Log.d("ERROR", e.message.toString())
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

            CoroutineScope(Dispatchers.IO).launch {
                if(error == 0) {
                    if (action == CREATE) {
                        // Dapatkan last insert id (Long, diubah ke Int)
                        id = kostDao.addKost(Kost(0, nama, fasilitas, harga)).toInt()

                        sendNotification1()
                        sendNotification2()
                        progressDone = 100
                        sendNotification3()
                    } else if (action == EDIT) {
                        kostDao.updateKost(Kost(id, nama, fasilitas, harga))
                    }

//                    val intent = Intent(this@CreateActivity, HomeActivity::class.java)
//                    intent.putExtra("fragment", "list")
//                    startActivity(intent)
//                    finish()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    // do nothing
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
        this.setEditDeleteBtn()
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
            CoroutineScope(Dispatchers.IO).launch {
                kostDao.deleteKost(id)
                withContext(Dispatchers.Main) {
//                    val intent = Intent(this@CreateActivity, HomeActivity::class.java)
//                    intent.putExtra("fragment", "list")
//                    startActivity(intent)
//                    finish()
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
        dialog.setNegativeButton("Tidak") { dialog, which ->
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setInputElements() {
        if(action == EDIT || action == READ) {
            CoroutineScope(Dispatchers.IO).launch {
                val kost = kostDao.getKostById(id)

                withContext(Dispatchers.Main) {
                    tilTambahNama.editText?.setText(kost.namaKost)
                    tilTambahFasilitas.editText?.setText(kost.fasilitas)
                    tilTambahHarga.editText?.setText(kost.harga.toString())

                    // set btnTambah text
                    btnTambah.text = "Update"
                }
            }
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

    private fun setEditDeleteBtn() {
        if(action == CREATE) {
            menu.findItem(R.id.action_edit).isVisible = false
            menu.findItem(R.id.action_delete).isVisible = false
        } else {
            menu.findItem(R.id.action_edit).isVisible = true
            menu.findItem(R.id.action_delete).isVisible = true
        }
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

    private fun sendNotification1(){
        val intent: Intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Handle notification click
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // File "NotificationReceiver.kt" menerima parameter "action" --> ke mana activity yang dituju, kemudian "data" --> data apa yang akan dikirimkan
        // Kasus ini: action = "show_kost" agar bisa melihat detail kost, kemudian data = id kost yang akan ditampilkan
         val broadcastIntent = Intent(this, NotificationReceiver::class.java)
        broadcastIntent.putExtra("action", "show_kost")
        broadcastIntent.putExtra("data", id)
        val actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_baseline_looks_one_24)
            .setContentTitle(tilTambahNama.editText?.text.toString())
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setColor(Color.BLUE)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setStyle(NotificationCompat.InboxStyle()
                .addLine("Kost : " + tilTambahNama.editText?.text.toString())
                .addLine("Harga : " + tilTambahHarga.editText?.text.toString())
                .addLine("Fasilitas : " + tilTambahFasilitas.editText?.text.toString()))
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher, "Lihat Selengkapnya", actionIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId1, builder.build())
        }
    }

    private fun sendNotification2(){
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_baseline_looks_one_24)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Kost " + tilTambahNama.editText?.text.toString() +
                        " ditambahkan kedalam aplikasi JogjaKita dengan harga yang sangat terjangkau yakni mulai dari Rp. " + tilTambahHarga.editText?.text.toString() + " dengan fasilitas yang cukup memadai yakni " + tilTambahFasilitas.editText?.text.toString()))
            .setPriority(NotificationCompat.PRIORITY_LOW)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId2, builder.build())
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
}