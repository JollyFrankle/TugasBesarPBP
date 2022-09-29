package com.example.tugasbesarpbp.main_ui

import android.app.Notification
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasbesarpbp.*
import com.example.tugasbesarpbp.room.Constant
import com.example.tugasbesarpbp.room.MainDB
import com.example.tugasbesarpbp.room.kost.Kost
import com.example.tugasbesarpbp.room.kost.KostDao
import com.example.tugasbesarpbp.rv_adapters.RVItemKostAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class ListItemFragment : Fragment() {
    lateinit var kostAdapter: RVItemKostAdapter
    private lateinit var rvItemKost: RecyclerView
    private lateinit var kostDao: KostDao
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var btnSearch: Button
    private lateinit var searchInput: TextInputLayout

    private val CHANNEL_ID_1 = "channel_notification_01"
    private val notificationId3 = 103

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db by lazy { MainDB(activity as HomeActivity) }
        kostDao = db.KostDao()

        rvItemKost = view.findViewById(R.id.rvItemKostContainer)
        btnAdd = view.findViewById(R.id.btnFloatAdd)
        btnSearch = view.findViewById(R.id.btnSearch)
        searchInput = view.findViewById(R.id.tilSearch)

        // set actionbar title
        (activity as HomeActivity).setActionBarTitle("Daftar Kost")

        btnAdd.setOnClickListener {
            val intent = Intent(activity, CreateActivity::class.java)
            intent.putExtra("action", CreateActivity.CREATE)
            intent.putExtra("id", 0)
//            startActivity(intent)
            (activity as HomeActivity).resultLauncher.launch(intent)
        }

        btnSearch.setOnClickListener {
            // refresh data
            this.loadData()
        }

        this.loadData()
        setupRecyclerView()
    }

    fun setupRecyclerView(){
        kostAdapter = RVItemKostAdapter(arrayListOf(), object: RVItemKostAdapter.OnAdapterListener{
            override fun onClick(kost: Kost){
                intentEdit(kost.id, CreateActivity.READ)
            }

//            override fun onUpdate(kost: Kost){
////                intentEdit(kost.id, Constant.TYPE_UPDATE)
//            }
//
//            override fun onDelete(kost: Kost){
////                deleteDialog(kost)
//            }
        })
        rvItemKost.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = kostAdapter
        }
    }

//    private fun deleteDialog(kost: Kost){
//        val alertDialog = AlertDialog.Builder((activity as HomeActivity))
//        alertDialog.apply {
//            setTitle("Confirmation")
//            setMessage("Are You Sure to delete this data From ${kost.id}?")
//            setNegativeButton("Cancel", DialogInterface.OnClickListener
//            { dialogInterface, i ->
//                dialogInterface.dismiss()
//            })
//            setPositiveButton("Delete", DialogInterface.OnClickListener
//            { dialogInterface, i ->
//                dialogInterface.dismiss()
//                CoroutineScope(Dispatchers.IO).launch {
//                    kostDao.deleteKost(kost.id)
//                    loadData()
//                }
//            })
//        }
//        alertDialog.show()
//    }

    private fun loadData() {
        val query = searchInput.editText?.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            val data = kostDao.getKost(query)
            withContext(Dispatchers.Main){
                kostAdapter.setData(data as ArrayList<Kost>)
            }
        }
    }

    private fun intentEdit(kostId: Int, intentType: Int){
        val intent = Intent(activity, CreateActivity::class.java)
        intent.putExtra("id", kostId)
        intent.putExtra("action", intentType)
//        startActivity(intent)
        (activity as HomeActivity).resultLauncher.launch(intent)
        // tutup activity home
//        (activity as HomeActivity).finish()
    }
}