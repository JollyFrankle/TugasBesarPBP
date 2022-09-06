package com.example.tugasbesarpbp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasbesarpbp.entity.ItemKost
import com.example.tugasbesarpbp.rv_adapters.RVItemKostAdapter

class ListItemFragment : Fragment() {

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
        val adapter: RVItemKostAdapter = RVItemKostAdapter(ItemKost.itemKosts)

        val rvItemKost: RecyclerView = view.findViewById(R.id.rvItemKostContainer)

        rvItemKost.layoutManager = layoutManager

        rvItemKost.adapter = adapter
    }
}