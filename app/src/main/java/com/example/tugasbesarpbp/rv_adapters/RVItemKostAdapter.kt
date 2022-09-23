package com.example.tugasbesarpbp.rv_adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasbesarpbp.R
import com.example.tugasbesarpbp.room.kost.Kost
import com.google.android.material.card.MaterialCardView
import java.net.URL
import java.util.*

class RVItemKostAdapter(private val data: ArrayList<Kost>, private val listener: OnAdapterListener) : RecyclerView.Adapter<RVItemKostAdapter.viewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_kost, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Kost>){
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItem = data[position]
        holder.namaEl.text = currentItem.namaKost
//        holder.tipeEl.text = currentItem.tipe
        holder.hargaEl.text = "Rp " + "%,.0f".format(Locale("id", "ID"), currentItem.harga)
//         loop through fasilitas
//        var fasilitas = ""
//        for (i in currentItem.fasilitas!!) {
//            fasilitas += i + " - "
//        }
        holder.fasilitasEl.text = currentItem.fasilitas


        // set image from url (currentItem.foto):
//        ItemKost.DownloadImageFromInternet(holder.imageEl).execute(currentItem.foto)

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
        fun onUpdate(kost: Kost)
        fun onDelete(kost: Kost)
    }
}