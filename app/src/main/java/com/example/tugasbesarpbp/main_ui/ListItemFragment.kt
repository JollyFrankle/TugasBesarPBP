package com.example.tugasbesarpbp.main_ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tugasbesarpbp.*
import com.example.tugasbesarpbp.api.KostApi
import com.example.tugasbesarpbp.api_models.Kost
import com.example.tugasbesarpbp.room.MainDB
import com.example.tugasbesarpbp.room.kost.KostDao
import com.example.tugasbesarpbp.rv_adapters.RVItemKostAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.ArrayList

class ListItemFragment : Fragment() {
    lateinit var kostAdapter: RVItemKostAdapter
    private lateinit var rvItemKost: RecyclerView
    private lateinit var kostDao: KostDao
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var btnSearch: Button
    private lateinit var searchInput: TextInputLayout
    private var queue: RequestQueue? = null

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

        queue = Volley.newRequestQueue(activity)
        rvItemKost = view.findViewById(R.id.rvItemKostContainer)
        btnAdd = view.findViewById(R.id.btnFloatAdd)
        btnSearch = view.findViewById(R.id.btnSearch)
        searchInput = view.findViewById(R.id.tilSearch)
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

//        btnSearch.setOnClickListener {
//            // refresh data
//            this.loadData()
//        }

//        this.loadData()
        setupRecyclerView()
    }

    fun setupRecyclerView(){
        kostAdapter = RVItemKostAdapter(arrayListOf(), object: RVItemKostAdapter.OnAdapterListener{
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
//        srMahasiswa!!.isRefreshing = true
        val stringRequest: StringRequest =
            object: StringRequest(Method.GET, KostApi.GET_ALL_URL, Response.Listener { response ->
                val gson = Gson()
                val jsonObject = JSONObject(response)
                var kost : Array<Kost> = gson.fromJson(
                    jsonObject.getJSONArray("data").toString(), Array<Kost>::class.java
                )

                kostAdapter.setKostList(kost)

                if(!kost.isEmpty())
                    Toast.makeText(activity as HomeActivity, "Data seluruh Kost berhasil diambil", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(activity as HomeActivity, "Data kosong", Toast.LENGTH_SHORT).show()
            }, Response.ErrorListener { error ->
//                srMahasiswa!!.isRefreshing = false

                try{
                    val responseBody = String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(
                        activity as CRUDKostActivity,
                        errors.getString("message"),
                        Toast.LENGTH_SHORT
                    ).show()
                } catch(e: Exception){
                    Toast.makeText(activity as HomeActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String>{
                    val headers = HashMap<String, String>()
                    headers["Accept"] = "application/json"
                    return headers
                }
            }
        queue!!.add(stringRequest)
    }

    override fun onStart() {
        super.onStart()
        allKost()

        // refresh data
//        this.loadData()
    }

//    private fun loadData() {
//        val query = searchInput.editText?.text.toString()
//        CoroutineScope(Dispatchers.IO).launch {
//            val data = kostDao.getKost(query)
//            withContext(Dispatchers.Main){
//                kostAdapter.setData(data as ArrayList<Kost>)
//            }
//        }
//    }

    private fun intentEdit(kostId: Long?, intentType: Int){
        val intent = Intent(activity, CRUDKostActivity::class.java)
        intent.putExtra("id", kostId)
        intent.putExtra("action", intentType)
        startActivity(intent)
    }
}