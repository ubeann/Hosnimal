package com.hosnimal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class OnBoardingActivity : AppCompatActivity() {
    // dataset
    private val adapter = com.hosnimal.OnBoardingAdapter(
        listOf(
            OnBoardingItem(
                "Perawatan Hewan",
                "Kami memberikan pelayanan, pemeriksaan serta pengobatan karena kesehatan dan keselamatan hewan anda\nmenjadi prioritas kami",
                R.raw.on_boarding_1_animal
            ),
            OnBoardingItem(
                "Farmasi",
                "Kami menyediakan makanan, obat, alat kesehatan dan kebutuhan hewan lainnya yang dibutuhkan secara online",
                R.raw.on_boarding_2_shopping
            ),
            OnBoardingItem(
                "Konsultasi Online",
                "Kami menyediakan tenaga medis berpengalaman untuk membantu mengatasi permasalahan hewan anda secara real-time dengan chat secara langsung",
                R.raw.on_boarding_3_consultation
            )
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        // Setting ViewPager2
        val content = findViewById<ViewPager2>(R.id.list_content)
        content.adapter = adapter

        // Setting button
        val btnNext:Button = findViewById<Button>(R.id.btn_next)
        val btnSkip:Button = findViewById<Button>(R.id.btn_skip)

        // Setting dotsIndicator
        val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)
        dotsIndicator.setViewPager2(content)

        // Lottie Animation
//        val animation:LottieAnimationView = findViewById(R.id.animation)

        // Dynamic Content
        content.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position+1 == adapter.itemCount) {
                    btnNext.text = "Mulai Sekarang"
                    btnSkip.visibility = View.GONE
                } else {
                    btnNext.text = "BERIKUTNYA"
                    btnSkip.visibility = View.VISIBLE
                }
            }
        })

        // Button Next
        btnNext.setOnClickListener{
            if(content.currentItem+1 < adapter.itemCount){
                content.currentItem += 1
            }else{
                val intent = Intent(
                    this@OnBoardingActivity,
                    RegisterActivity::class.java
                )
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }
        }

        // Button Skip
        btnSkip.setOnClickListener {
            content.currentItem = adapter.itemCount
        }
    }
}