package com.example.market

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.market.databinding.ActivityEditBinding
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private var firestore: FirebaseFirestore? = null
    private var product: Product? = null
    private var documentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        product = intent.getParcelableExtra("productModel")
        documentId = intent.getStringExtra("documentId")

        binding.priceTextView2.hint = product?.price
        binding.sellTextView2.hint = product?.sell

        binding.button4.setOnClickListener {
            updateProduct()
        }
    }

    private fun updateProduct() {
        var modifiedPrice = binding.priceTextView2.text.toString()
        var modifiedSellStatus = binding.sellTextView2.text.toString()

        if (modifiedPrice.isBlank()) {
            modifiedPrice = product?.price.orEmpty()
        }
        if (modifiedSellStatus.isBlank()) {
            modifiedSellStatus = product?.sell.orEmpty()
        }

        val productRef = firestore?.collection("products")?.document(documentId!!)
        productRef?.update(
            mapOf(
                "price" to modifiedPrice,
                "sell" to modifiedSellStatus
            )
        )?.addOnSuccessListener {
            finish()
        }?.addOnFailureListener {
            // Handle failure (e.g., show an error message)
        }
    }
}
