package com.example.tugasbesarpbp.main_ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tugasbesarpbp.*
import com.example.tugasbesarpbp.api.http.KostApi
import com.example.tugasbesarpbp.api.models.Kost
import com.example.tugasbesarpbp.room.MainDB
import com.example.tugasbesarpbp.room.kost.KostDao
import com.example.tugasbesarpbp.rv_adapters.RVItemKostAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class ListItemFragment : Fragment() {
    lateinit var kostAdapter: RVItemKostAdapter
    private lateinit var rvItemKost: RecyclerView
    private lateinit var kostDao: KostDao
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var btnSearch: Button
    private lateinit var searchInput: TextInputLayout
    private lateinit var srItemKost: SwipeRefreshLayout
    private var queue: RequestQueue? = null

    private lateinit var token: String
    private var userId: Long = 0

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

        queue = Volley.newRequestQueue(activity)
        rvItemKost = view.findViewById(R.id.rvItemKostContainer)
        btnAdd = view.findViewById(R.id.btnFloatAdd)
        btnSearch = view.findViewById(R.id.btnSearch)
        searchInput = view.findViewById(R.id.tilSearch)
        srItemKost = view.findViewById(R.id.srItemKost)

        // arguments
        arguments.let {
            searchInput.editText?.setText(it?.getString("search", ""))
        }

        // set actionbar title
        (activity as HomeActivity).setActionBarTitle("Daftar Kost")

        btnAdd.setOnClickListener {
            val intent = Intent(activity, CRUDKostActivity::class.java)
            intent.putExtra("action", CRUDKostActivity.CREATE)
            intent.putExtra("id", 0)
            startActivity(intent)
        }

        srItemKost.isRefreshing = true
        srItemKost.setOnRefreshListener {
            allKost()
        }

        // get token
        (activity as HomeActivity).getSharedPreferences("session", Context.MODE_PRIVATE).let {
            token = it.getString("token", "")!!
            userId = it.getLong("id", 0)
        }

        btnSearch.setOnClickListener {
            // refresh data
            allKost()
        }

        // load data
        setupRecyclerView()
    }

    fun setupRecyclerView(){
        kostAdapter = RVItemKostAdapter(arrayListOf(), userId, object: RVItemKostAdapter.OnAdapterListener{
            override fun onClick(kost: Kost){
                intentEdit(kost.id, CRUDKostActivity.READ)
            }
        })
        rvItemKost.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = kostAdapter
        }
    }

    private fun allKost(){
        srItemKost.isRefreshing = true
        val search = searchInput.editText?.text.toString()
        KostApi.getAllKosts(requireActivity(), search, { response ->
            val gson = Gson()
            val jsonObject = JSONObject(response)
            val kost = gson.fromJson(
                jsonObject.getJSONArray("data").toString(), Array<Kost>::class.java
            )
            kostAdapter.setData(kost.toCollection(ArrayList()))
            srItemKost.isRefreshing = false
        })
    }

    override fun onStart() {
        super.onStart()

        // refresh data
        this.allKost()
    }

    private fun intentEdit(kostId: Long?, intentType: Int){
        val intent = Intent(activity, CRUDKostActivity::class.java)
        intent.putExtra("id", kostId)
        intent.putExtra("action", intentType)
        startActivity(intent)
    }
}