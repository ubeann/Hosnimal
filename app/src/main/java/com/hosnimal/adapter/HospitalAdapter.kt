package com.hosnimal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.hosnimal.R
import com.hosnimal.model.Hospital
import java.time.OffsetTime

class HospitalAdapter(private val listHospital: List<Hospital>) : RecyclerView.Adapter<HospitalAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        // Component
        var hospitalImage: ImageView = itemView.findViewById(R.id.image)
        var hospitalName: TextView = itemView.findViewById(R.id.name)
        var hospitalLocation: TextView = itemView.findViewById(R.id.location)
        var hospitalOpen: TextView = itemView.findViewById(R.id.status_open)
        var hospitalClosed: TextView = itemView.findViewById(R.id.status_closed)

        // Loading
        private val shimmer = Shimmer.AlphaHighlightBuilder()
            .setDuration(1800)
            .setBaseAlpha(0.7f)
            .setHighlightAlpha(0.6f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()
        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder = ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_hospital, parent, false))

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val isOpen = listHospital[position].open.isBefore(OffsetTime.now()) and listHospital[position].close.isAfter(OffsetTime.now())
        Glide.with(holder.hospitalImage.context)
            .load(listHospital[position].images.first())
            .placeholder(holder.shimmerDrawable)
            .error(R.drawable.ic_round_error_24)
            .into(holder.hospitalImage)
        holder.hospitalName.text = listHospital[position].name
        holder.hospitalLocation.text = listHospital[position].location
        holder.hospitalOpen.visibility = if (isOpen) View.VISIBLE else View.INVISIBLE
        holder.hospitalClosed.visibility = if (isOpen) View.INVISIBLE else View.VISIBLE
        holder.itemView.setOnClickListener{ onItemClickCallback.onItemClicked(listHospital[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = listHospital.size

    interface OnItemClickCallback {
        fun onItemClicked(data: Hospital)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}