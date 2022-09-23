package com.example.tugasbesarpbp

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
import com.example.tugasbesarpbp.entity.ItemKost
import com.example.tugasbesarpbp.room.Constant
import com.example.tugasbesarpbp.room.MainDB
import com.example.tugasbesarpbp.room.kost.Kost
import com.example.tugasbesarpbp.rv_adapters.RVItemKostAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList

class ListItemFragment : Fragment() {
        lateinit var kostAdapter: RVItemKostAdapter
        private val db by lazy { MainDB(activity as MainActivity) }
        private val kostDao = db.KostDao()
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_list_item, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set actionbar title
        (activity as HomeActivity).setActionBarTitle("Daftar Kost")

        // enable back button
        (activity as HomeActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutManager = LinearLayoutManager(context)
//        val adapter = RVItemKostAdapter(ItemKost.itemKosts)

        val rvItemKost: RecyclerView = view.findViewById(R.id.rvItemKostContainer)
        setupRecyclerView()
        rvItemKost.layoutManager = layoutManager

//        rvItemKost.adapter = adapter
    }

    fun setupRecyclerView(){
        val rvItemKost: RecyclerView = requireView().findViewById(R.id.rvItemKostContainer)
        kostAdapter = RVItemKostAdapter(arrayListOf(), object: RVItemKostAdapter.OnAdapterListener{
            override fun onClick(kost: ItemKost){
                intentEdit(kost.id, Constant.TYPE_READ)
            }

            override fun onUpdate(kost: Kost){
                intentEdit(kost.id, Constant.TYPE_UPDATE)
            }

            override fun onDelete(kost: Kost){
                deleteDialog(kost)
            }
        })
        rvItemKost.apply{
            layoutManager = LinearLayoutManager((activity as HomeActivity))
            adapter = kostAdapter
        }
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
                    db.KostDao().deleteKost(kost)
                    loadData()
                }
            })
        }
        alertDialog.show()
    }

    fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val data = db.KostDao().getKost()
            Log.d("MainActivity","dbResponse: $data")
            withContext(Dispatchers.Main){
                kostAdapter.setData(data as ArrayList<Kost>)
            }
        }
    }

    private fun intentEdit(kostId: Int, intentType: Int){
        startActivity(
            Intent((activity as HomeActivity), EditViewActivity::class.java)
                .putExtra("intent_id", kostId)
                .putExtra("intent_type", intentType)
        )
    }
}