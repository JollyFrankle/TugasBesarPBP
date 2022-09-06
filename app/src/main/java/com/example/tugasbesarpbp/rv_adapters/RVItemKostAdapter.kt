package com.example.tugasbesarpbp.rv_adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasbesarpbp.R
import com.example.tugasbesarpbp.entity.ItemKost
import java.net.URL
import java.util.*

class RVItemKostAdapter(private val data: Array<ItemKost>) : RecyclerView.Adapter<RVItemKostAdapter.viewHolder>() {
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

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItem = data[position]
        holder.namaEl.text = currentItem.nama
        holder.tipeEl.text = currentItem.tipe
        holder.hargaEl.text = "Rp " + "%,.0f".format(Locale("id", "ID"), currentItem.harga)
        // loop through fasilitas
        var fasilitas = ""
        for (i in currentItem.fasilitas!!) {
            fasilitas += i + " - "
        }
        holder.fasilitasEl.text = fasilitas

        // set image from url (currentItem.foto):
        ItemKost.DownloadImageFromInternet(holder.imageEl).execute(currentItem.foto)
//        val imageUri: Uri = Uri.parse(currentItem.foto)
//        val bmp: Bitmap = BitmapFactory.decodeStream(URL(currentItem.foto).openConnection().getInputStream())
//        holder.imageEl.setImageBitmap(bmp)
//        holder.imageEl.setImageURI(imageUri)

//        holder.imageEl.setImageURI(Uri.parse(currentItem.foto))
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tipeEl: TextView = itemView.findViewById(R.id.txtRVItemKostTipe)
        val hargaEl: TextView = itemView.findViewById(R.id.txtRVItemKostPrice)
        val fasilitasEl: TextView = itemView.findViewById(R.id.txtRVItemKostFasilitas)
        val namaEl: TextView = itemView.findViewById(R.id.txtRVItemKostNama)
        val imageEl: ImageView = itemView.findViewById(R.id.imgRVItemKostImage)
    }
}