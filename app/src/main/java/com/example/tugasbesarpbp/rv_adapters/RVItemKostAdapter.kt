package com.example.tugasbesarpbp.rv_adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasbesarpbp.R
import com.example.tugasbesarpbp.api_models.Kost
import com.google.android.material.card.MaterialCardView
import java.util.*

class RVItemKostAdapter(private var kostList: ArrayList<Kost>, private val listener: OnAdapterListener) : RecyclerView.Adapter<RVItemKostAdapter.viewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_kost, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return kostList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Kost>){
        kostList.clear()
        kostList.addAll(list)
        notifyDataSetChanged()
    }

    fun setKostList(kostList: Array<Kost>){
        this.kostList = kostList.toList() as ArrayList<Kost>
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItem = kostList[position]
        holder.namaEl.text = currentItem.namaKost
        holder.hargaEl.text = "Rp " + "%,.0f".format(Locale("id", "ID"), currentItem.harga)
        holder.fasilitasEl.text = currentItem.fasilitas

        holder.cardClicked.setOnClickListener {
            listener.onClick(currentItem)
        }
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tipeEl: TextView = itemView.findViewById(R.id.txtRVItemKostTipe)
        val hargaEl: TextView = itemView.findViewById(R.id.txtRVItemKostPrice)
        val fasilitasEl: TextView = itemView.findViewById(R.id.txtRVItemKostFasilitas)
        val namaEl: TextView = itemView.findViewById(R.id.txtRVItemKostNama)
        val imageEl: ImageView = itemView.findViewById(R.id.imgRVItemKostImage)
        val cardClicked: MaterialCardView = itemView.findViewById((R.id.cardView))
    }

    interface OnAdapterListener{
        fun onClick(kost: Kost)
//        fun onUpdate(kost: Kost)
//        fun onDelete(kost: Kost)
    }
}