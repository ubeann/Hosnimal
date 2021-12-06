package com.hosnimal.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.hosnimal.R

class ImageSliderAdapter(private val Images: List<String>) : RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageSliderAdapter.ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_image_slider, parent, false))

    override fun getItemCount(): Int = Images.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(Images[position])
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        // Component
        private val productImage: ImageView = view.findViewById(R.id.image)

        // Loading
        private val shimmer = Shimmer.AlphaHighlightBuilder()
            .setDuration(1800)
            .setBaseAlpha(0.7f)
            .setHighlightAlpha(0.6f)
            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
            .setAutoStart(true)
            .build()
        private val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }

        fun bind(image: String) {
            Glide.with(productImage.context)
                .load(image)
                .placeholder(shimmerDrawable)
                .error(R.drawable.ic_round_error_24)
                .into(productImage)
        }
    }
}