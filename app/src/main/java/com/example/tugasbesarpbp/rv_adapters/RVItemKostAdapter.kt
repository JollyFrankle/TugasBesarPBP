package com.example.tugasbesarpbp.rv_adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasbesarpbp.R
import com.example.tugasbesarpbp.api.models.Kost
import com.google.android.material.card.MaterialCardView
import java.util.*

class RVItemKostAdapter(private var kostList: ArrayList<Kost>, private val userId: Long, private val listener: OnAdapterListener) : RecyclerView.Adapter<RVItemKostAdapter.viewHolder>() {
    private val locale = Locale("id", "ID")
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

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItem = kostList[position]
        holder.namaEl.text = currentItem.namaKost
        holder.hargaEl.text = "Rp " + "%,.0f".format(locale, currentItem.harga)
        holder.fasilitasEl.text = currentItem.fasilitas
        holder.tipeEl.text = "Kost ${currentItem.tipe}"

        holder.cardClicked.setOnClickListener {
            listener.onClick(currentItem)
        }

        if(currentItem.idPemilik != userId) {
            holder.badgeMilikAnda.visibility = View.GONE
        } else {
            holder.badgeMilikAnda.visibility = View.VISIBLE
        }

        println("ID Pemilik: ${currentItem.idPemilik}, ID User: $userId")
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tipeEl: TextView = itemView.findViewById(R.id.txtRVItemKostTipe)
        val hargaEl: TextView = itemView.findViewById(R.id.txtRVItemKostPrice)
        val fasilitasEl: TextView = itemView.findViewById(R.id.txtRVItemKostFasilitas)
        val namaEl: TextView = itemView.findViewById(R.id.txtRVItemKostNama)
        val imageEl: ImageView = itemView.findViewById(R.id.imgRVItemKostImage)
        val cardClicked: MaterialCardView = itemView.findViewById((R.id.cardView))
        val badgeMilikAnda: LinearLayout = itemView.findViewById(R.id.badgeMilikAnda)
    }

    interface OnAdapterListener{
        fun onClick(kost: Kost)
    }
}