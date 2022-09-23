package com.example.tugasbesarpbp.main_ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasbesarpbp.*
import com.example.tugasbesarpbp.room.Constant
import com.example.tugasbesarpbp.room.MainDB
import com.example.tugasbesarpbp.room.kost.Kost
import com.example.tugasbesarpbp.room.kost.KostDao
import com.example.tugasbesarpbp.rv_adapters.RVItemKostAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class ListItemFragment : Fragment() {
    lateinit var kostAdapter: RVItemKostAdapter
    private lateinit var rvItemKost: RecyclerView
    private lateinit var kostDao: KostDao

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

        // set actionbar title
        (activity as HomeActivity).setActionBarTitle("Daftar Kost")

        this.loadData()
        setupRecyclerView()
    }

    fun setupRecyclerView(){
        kostAdapter = RVItemKostAdapter(arrayListOf(), object: RVItemKostAdapter.OnAdapterListener{
            override fun onClick(kost: Kost){
                intentEdit(kost.id, CreateActivity.READ)
            }

            override fun onUpdate(kost: Kost){
//                intentEdit(kost.id, Constant.TYPE_UPDATE)
            }

            override fun onDelete(kost: Kost){
//                deleteDialog(kost)
            }
        })
        rvItemKost.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = kostAdapter
        }
        println(kostAdapter.getItemCount().toString() + "------------------------------------------------------------------------------")
    }

    private fun deleteDialog(kost: Kost){
        val alertDialog = AlertDialog.Builder((activity as HomeActivity))
        alertDialog.apply {
            setTitle("Confirmation")
            setMessage("Are You Sure to delete this data From ${kost.id}?")
            setNegativeButton("Cancel", DialogInterface.OnClickListener
            { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            setPositiveButton("Delete", DialogInterface.OnClickListener
            { dialogInterface, i ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    kostDao.deleteKost(kost.id)
                    loadData()
                }
            })
        }
        alertDialog.show()
    }

    fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = kostDao.getKost()
            withContext(Dispatchers.Main){
                kostAdapter.setData(data as ArrayList<Kost>)
            }
        }
    }

    private fun intentEdit(kostId: Int, intentType: Int){
        val intent = Intent(activity, CreateActivity::class.java)
        intent.putExtra("id", kostId)
        intent.putExtra("action", intentType)
        startActivity(intent)
    }
}