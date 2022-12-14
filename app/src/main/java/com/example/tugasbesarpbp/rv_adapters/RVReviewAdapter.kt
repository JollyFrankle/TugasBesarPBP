package com.example.tugasbesarpbp.rv_adapters

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.tugasbesarpbp.R
import com.example.tugasbesarpbp.api.models.Review
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class RVReviewAdapter(private var list: ArrayList<Review>, private val listener: OnAdapterListener) : RecyclerView.Adapter<RVReviewAdapter.viewHolder>() {
    private val locale = Locale("id", "ID")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_review, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<Review>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentItem = list[position]

        // convert currentItem.created_at from string to date with locale id_ID
        val instant = Instant.parse(currentItem.created_at)
        val date = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())

        holder.ratingBar.rating = currentItem.rating
        holder.review.text = currentItem.review
        holder.tanggal.text = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", locale))
        holder.nama.text = currentItem.users!!.nama
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ratingBar: RatingBar = itemView.findViewById(R.id.txtRVIR_RatingBar)
        val tanggal: TextView = itemView.findViewById(R.id.txtRVIR_Tanggal)
        val nama: TextView = itemView.findViewById(R.id.txtRVIR_Nama)
        val review: TextView = itemView.findViewById(R.id.txtRVIR_Review)
    }

    interface OnAdapterListener{
        fun onClick(item: Review)
    }
}