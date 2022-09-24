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
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tugasbesarpbp.databinding.ActivityCreateBinding
import com.example.tugasbesarpbp.room.MainDB
import com.example.tugasbesarpbp.room.kost.Kost
import com.example.tugasbesarpbp.room.kost.KostDao
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateActivity : AppCompatActivity() {

    private var binding: ActivityCreateBinding? = null

    private lateinit var btnTambah: Button
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var tilTambahNama: TextInputLayout
    private lateinit var tilTambahFasilitas: TextInputLayout
    private lateinit var tilTambahHarga: TextInputLayout


    private lateinit var builderManager: NotificationManagerCompat
    private val CHANNEL_ID_1 = "channel_notification_01"
    private val notificationId1 = 101
    private val CHANNEL_ID_2 = "channel_notification_02"
    private val notificationId2 = 102

    companion object {
        const val CREATE = 1
        const val EDIT = 2
        const val READ = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        val builderManager = NotificationManagerCompat.from(this)

        createNotificationChannel()

        setTitle("Data Kost")

        // get action from intent
        var action = intent.getIntExtra("action", 3)
        val id = intent.getIntExtra("id", 1)

        btnTambah = findViewById(R.id.btnTambah)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)
        tilTambahNama = findViewById(R.id.tilTambahNama)
        tilTambahFasilitas = findViewById(R.id.tilTambahFasilitas)
        tilTambahHarga = findViewById(R.id.tilTambahHarga)
//        val tilNama = binding.tilTambahNama.editText?.text.toString()

        val db by lazy { MainDB(this) }
        val kostDao = db.KostDao()

        this.setInputElements(action, id, kostDao);

        btnEdit.setOnClickListener {
            if(action == EDIT) {
                action = READ
            } else {
                action = EDIT
            }
            setInputElements(action, id, kostDao)
        }

        btnDelete.setOnClickListener {
            // confirm alert
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Konfirmasi")
            dialog.setMessage("Apakah anda yakin ingin menghapus data ini?")
            dialog.setPositiveButton("Ya") { dialog, which ->
                CoroutineScope(Dispatchers.IO).launch {
                    kostDao.deleteKost(id)
                    withContext(Dispatchers.Main) {
                        val intent = Intent(this@CreateActivity, HomeActivity::class.java)
                        intent.putExtra("fragment", "list")
                        startActivity(intent)
                        finish()
                    }
                }
            }
            dialog.setNegativeButton("Tidak") { dialog, which ->
                dialog.dismiss()
            }
            dialog.show()
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
                        kostDao.addKost(Kost(0, nama, fasilitas, harga))
                        sendNotification1()
                        sendNotification2()
                    } else if (action == EDIT) {
                        kostDao.updateKost(Kost(id, nama, fasilitas, harga))
                    }

                    val intent = Intent(this@CreateActivity, HomeActivity::class.java)
                    intent.putExtra("fragment", "list")
                    startActivity(intent)
                    finish()
                } else {
                    // do nothing
                }
//                Log.d("MASUK DB", "DAO")
//                val kost: Kost = Kost(0, tilTambahNama.editText?.text.toString(), tilTambahAlamat.editText?.text.toString(), tilTambahFasilitas.editText?.text.toString())
//                kostDao.addKost(kost)
            }
        }
    }

    private fun setInputElements(action: Int, id: Int, kostDao: KostDao) {
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
        val pendingIntent : PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val broadcastIntent: Intent = Intent(this, NotificationReceiver::class.java)
        broadcastIntent.putExtra("toastMessage", tilTambahNama.toString())
        val actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_baseline_looks_one_24)
            .setContentTitle(tilTambahNama.editText?.text.toString())
                // setContentText untuk menampilkan text dibawah title dengan singkat
//            .setContentText("Kost baru ditambahkan dengan harga Rp."+ tilTambahHarga.editText?.text.toString())
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setColor(Color.BLUE)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setStyle(NotificationCompat.InboxStyle()
                .addLine("Kost : " + tilTambahNama.editText?.text.toString())
                .addLine("Harga : " + tilTambahHarga.editText?.text.toString())
                .addLine("Fasilitas : " + tilTambahFasilitas.editText?.text.toString()))
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
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
                        " ditambahkan kedalam aplikasi JogjaKita dengan harga yang sangat terjangkau yakni mulai dari Rp." + tilTambahHarga.editText?.text.toString() + " dengan fasilitas yang cukup memadai yakni" + tilTambahFasilitas.editText?.text.toString()))
            .setPriority(NotificationCompat.PRIORITY_LOW)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId2, builder.build())
        }
    }

//    private fun sendNotification3(){
//        val pendingIntent : PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
//        val progressMax = 100
//        val builder = NotificationCompat.Builder(this, CHANNEL_ID_1)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setOngoing(true)
//            .setOnlyAlertOnce(true)
//            .setProgress(progressMax, 0, true)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//        builderManager.notify(1, builder.build())
//
//        Thread(Runnable{
//            SystemClock.sleep(2000)
//            var progress = 0
//            while (progress <= progressMax) {
//                SystemClock.sleep(
//                    1000
//                )
//                progress += 20
//                //Use this to make it a Fixed-duration progress indicator notification
//
//                //notification.setContentText(progress.toString()+"%")
//                //.setProgress(progressMax, progress, false)
//
//                //notificationManager.notify(1, notification.build())
//            }
//
//            builder.setContentText("Download complete")
//                .setProgress(0, 0, false)
//                .setOngoing(false)
//            builderManager.notify(1, builder.build())
//        }).start()
//
//        with(NotificationManagerCompat.from(this)){
//            notify(notificationId2, builder.build())
//        }
//    }
}