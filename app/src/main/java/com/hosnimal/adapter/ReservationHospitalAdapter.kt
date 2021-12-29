package com.hosnimal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hosnimal.R
import com.hosnimal.model.relational.HospitalReservation
import java.time.format.DateTimeFormatter
import java.util.*

class ReservationHospitalAdapter(private val listReservation: List<HospitalReservation>, private val userId: Int) : RecyclerView.Adapter<ReservationHospitalAdapter.ListViewHolder>() {
    private lateinit var onBtnClickCallback: OnBtnClickCallback

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        // Component
        var reservationImage: ImageView = itemView.findViewById(R.id.image)
        var reservationName: TextView = itemView.findViewById(R.id.name)
        var reservationDate: TextView = itemView.findViewById(R.id.date)
        var reservationStart: TextView = itemView.findViewById(R.id.time_start)
        var reservationEnd: TextView = itemView.findViewById(R.id.time_end)
        var btnCancel: Button = itemView.findViewById(R.id.btn_cancel)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        // Set Data
        Glide.with(holder.reservationImage.context)
            .load(R.drawable.avatar)
            .error(R.drawable.ic_round_error_24)
            .into(holder.reservationImage)
        holder.reservationName.text = listReservation[position].user.name
        holder.reservationDate.text = listReservation[position].detailReservation.start.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("in", "ID")))
        holder.reservationStart.text = listReservation[position].detailReservation.start.format(DateTimeFormatter.ofPattern("HH:mm", Locale("in", "ID")))
        holder.reservationEnd.text = listReservation[position].detailReservation.end.format(DateTimeFormatter.ofPattern("HH:mm", Locale("in", "ID")))

        // Button Cancel
        holder.btnCancel.visibility = if (listReservation[position].detailReservation.userId == userId) View.VISIBLE else View.GONE
        holder.btnCancel.setOnClickListener { onBtnClickCallback.onBtnClicked(listReservation[holder.adapterPosition]) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder = ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_reservation, parent, false))

    override fun getItemCount(): Int = listReservation.size

    interface OnBtnClickCallback {
        fun onBtnClicked(data: HospitalReservation)
    }

    fun setOnClickCallback(onBtnClickCallback: OnBtnClickCallback) {
        this.onBtnClickCallback = onBtnClickCallback
    }
}