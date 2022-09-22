package com.example.tugasbesarpbp

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.tugasbesarpbp.room.MainDB
import com.example.tugasbesarpbp.room.Constant
import com.example.tugasbesarpbp.room.user.User
//import kotlinx.android.synthetic.main.activity_update.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateActivity : AppCompatActivity() {
//    val db by lazy { MainDB(this) }
//    private var UserId: Int = 0
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_update)
//        setupView()
//        setupListener()
//
//    }
//    fun setupView(){
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        val intentType = intent.getIntExtra("intent_type", 0)
//        when (intentType){
//            Constant.TYPE_CREATE -> {
//                button_update.visibility = View.GONE
//            }
//            Constant.TYPE_READ -> {
//                button_save.visibility = View.GONE
//                button_update.visibility = View.GONE
//                getUser()
//            }
//            Constant.TYPE_UPDATE -> {
//                button_save.visibility = View.GONE
//                button_update.visibility = View.GONE
//                getUser()
//            }
//        }
//    }
//    private fun setupListener() {
//        button_save.setOnClickListener {
//            CoroutineScope(Dispatchers.IO).launch{
//                db.UserDao().addUser(
//                    User(0,edit_username.text.toString(),
//                        edit_pass.text.toString(),
//                        edit_email.text.toString(),
//                        edit_date.text.toString(),
//                        edit_num.text.toString())
//                )
//                finish()
//            }
//        }
//        button_update.setOnClickListener {
//            CoroutineScope(Dispatchers.IO).launch {
//                db.UserDao().updateUser(
//                    User(UserId, edit_username.text.toString(),
//                        edit_pass.text.toString(),
//                        edit_email.text.toString(),
//                        edit_date.text.toString(),
//                        edit_num.text.toString())
//                )
//                finish()
//            }
//        }
//    }
//    fun getUser() {
//        UserId = intent.getIntExtra("intent_id", 0)
//        CoroutineScope(Dispatchers.IO).launch {
//            val users = db.UserDao().getUser(UserId)[0]
//            edit_username.setText(users.username)
//            edit_pass.setText(users.password)
//            edit_email.setText(users.email)
//            edit_date.setText(users.tanggalLahir)
//            edit_num.setText(users.nomorTelepon)
//        }
//    }
//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
//        return super.onSupportNavigateUp()
//    }
}