package com.hosnimal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

class OnBoardingAdapter(private val OnBoardingItem: List<OnBoardingItem>) :
    RecyclerView.Adapter<OnBoardingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.activity_on_boarding_slider, parent, false)
        )
    }
    override fun getItemCount(): Int {
        return OnBoardingItem.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(OnBoardingItem[position])
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val textTitle:TextView = view.findViewById(R.id.textTitle)
        private val textDescription:TextView = view.findViewById(R.id.textDescription)
        private val animation:LottieAnimationView = view.findViewById(R.id.animation)
        fun bind(OnBoardingItem: OnBoardingItem){
            textTitle.text = OnBoardingItem.title
            textDescription.text = OnBoardingItem.description
            animation.setAnimation(OnBoardingItem.animation)
        }
    }
}