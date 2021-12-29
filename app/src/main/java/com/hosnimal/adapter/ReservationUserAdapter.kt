package com.hosnimal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.hosnimal.R
import com.hosnimal.model.relational.UserReservation
import java.time.format.DateTimeFormatter
import java.util.*

class ReservationUserAdapter(private val listReservation: List<UserReservation>) : RecyclerView.Adapter<ReservationUserAdapter.ListViewHolder>() {
    private lateinit var onCardClickCallback: OnCardClickCallback
    private lateinit var onBtnClickCallback: OnBtnClickCallback

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        // Component
        var reservationCard: CardView = itemView.findViewById(R.id.reservation)
        var reservationImage: ImageView = itemView.findViewById(R.id.image)
        var reservationName: TextView = itemView.findViewById(R.id.name)
        var reservationDate: TextView = itemView.findViewById(R.id.date)
        var reservationStart: TextView = itemView.findViewById(R.id.time_start)
        var reservationEnd: TextView = itemView.findViewById(R.id.time_end)
        var btnCancel: Button = itemView.findViewById(R.id.btn_cancel)

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

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        // Set Data
        Glide.with(holder.reservationImage.context)
            .load(listReservation[position].hospital.images.first())
            .placeholder(holder.shimmerDrawable)
            .error(R.drawable.ic_round_error_24)
            .into(holder.reservationImage)
        holder.reservationName.text = listReservation[position].hospital.name
        holder.reservationDate.text = listReservation[position].detailReservation.start.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("in", "ID")))
        holder.reservationStart.text = listReservation[position].detailReservation.start.format(DateTimeFormatter.ofPattern("HH:mm", Locale("in", "ID")))
        holder.reservationEnd.text = listReservation[position].detailReservation.end.format(DateTimeFormatter.ofPattern("HH:mm", Locale("in", "ID")))

        // Button Cancel
        holder.btnCancel.visibility = View.VISIBLE
        holder.btnCancel.setOnClickListener { onBtnClickCallback.onBtnClicked(listReservation[holder.adapterPosition]) }

        // Card Click Listener
        holder.reservationCard.setOnClickListener { onCardClickCallback.onCardClicked(listReservation[holder.adapterPosition]) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder = ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_reservation, parent, false))

    override fun getItemCount(): Int = listReservation.size

    interface OnCardClickCallback {
        fun onCardClicked(data: UserReservation)
    }

    interface OnBtnClickCallback {
        fun onBtnClicked(data: UserReservation)
    }

    fun setOnClickCallback(onCardClickCallback: OnCardClickCallback, onBtnClickCallback: OnBtnClickCallback) {
        this.onCardClickCallback = onCardClickCallback
        this.onBtnClickCallback = onBtnClickCallback
    }
}