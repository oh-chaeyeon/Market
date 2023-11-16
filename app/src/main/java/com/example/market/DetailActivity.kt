package com.example.market

import android.os.Parcelable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.market.databinding.ActivityDetailBinding


@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val product = intent.getParcelableExtra<Product>("productModel")

        binding.titleTextView.text = product?.title.orEmpty()
        binding.priceTextView.text = product?.price.orEmpty() +"원"
        binding.contentTextView.text=product?.content.orEmpty()
        binding.sellTextView.text=product?.sell.orEmpty()

        Glide.with(binding.root.context)
            .load(product?.imageUrl)
            .into(binding.imageView3)
    }
}

