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
import com.hosnimal.model.Product
import com.hosnimal.model.relational.UserOrder
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.*

class OrderAdapter(private val listOrder: List<UserOrder>) : RecyclerView.Adapter<OrderAdapter.ListViewHolder>() {
    private lateinit var onCardClickCallback: OnCardClickCallback
    private lateinit var onBtnClickCallback: OnBtnClickCallback

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        // Component
        var orderCard: CardView = itemView.findViewById(R.id.order)
        var orderImage: ImageView = itemView.findViewById(R.id.image)
        var orderProduct: TextView = itemView.findViewById(R.id.product)
        var orderDate: TextView = itemView.findViewById(R.id.date)
        var orderPrice: TextView = itemView.findViewById(R.id.price)
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
        Glide.with(holder.orderImage.context)
            .load(listOrder[position].product.images.first())
            .placeholder(holder.shimmerDrawable)
            .error(R.drawable.ic_round_error_24)
            .into(holder.orderImage)
        holder.orderProduct.text = String.format("%dx %s", listOrder[position].detailOrder.qty, listOrder[position].product.name)
        holder.orderDate.text = listOrder[position].detailOrder.orderAt.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale("in", "ID")))
        holder.orderPrice.text = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(listOrder[position].product.price * listOrder[position].detailOrder.qty).toString().replace(",00","")

        // Set OnClick Listener
        holder.orderCard.setOnClickListener { onCardClickCallback.onCardClicked(listOrder[holder.adapterPosition].product) }
        holder.btnCancel.setOnClickListener { onBtnClickCallback.onBtnClicked(listOrder[holder.adapterPosition]) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder = ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_order, parent, false))

    override fun getItemCount(): Int = listOrder.size

    interface OnCardClickCallback {
        fun onCardClicked(data: Product)
    }

    interface OnBtnClickCallback {
        fun onBtnClicked(data: UserOrder)
    }

    fun setOnClickCallback(onCardClickCallback: OnCardClickCallback, onBtnClickCallback: OnBtnClickCallback) {
        this.onCardClickCallback = onCardClickCallback
        this.onBtnClickCallback = onBtnClickCallback
    }
}