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
import com.hosnimal.model.Product
import java.text.NumberFormat
import java.util.*

class ProductAdapter(private val listProduct: List<Product>) : RecyclerView.Adapter<ProductAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        // Component
        var productImage: ImageView = itemView.findViewById(R.id.image)
        var productName: TextView = itemView.findViewById(R.id.name)
        var productCategory: TextView = itemView.findViewById(R.id.category)
        var productPrice: TextView = itemView.findViewById(R.id.price)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder = ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_product, parent, false))

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        Glide.with(holder.productImage.context)
            .load(listProduct[position].images.first())
            .placeholder(holder.shimmerDrawable)
            .error(R.drawable.ic_round_error_24)
            .into(holder.productImage)
        holder.productName.text = listProduct[position].name
        holder.productCategory.text = listProduct[position].category
        holder.productPrice.text = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(listProduct[position].price).toString().replace(",00","")
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listProduct[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = listProduct.size

    interface OnItemClickCallback {
        fun onItemClicked(data: Product)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

}