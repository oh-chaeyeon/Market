package com.example.market

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.market.databinding.ActivityEditBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private var firestore: FirebaseFirestore? = null
    private var product: Product? = null
    private var documentId: String? = null

    @Suppress("DEPRECATION")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Retrieve the product details and document ID from the intent
        product = intent.getParcelableExtra("productModel")
        documentId = intent.getStringExtra("documentId")

        // Display the current price and sell status
        binding.priceTextView2.text = product?.price.orEmpty()
        binding.sellTextView2.text = product?.sell.orEmpty()

        // Set up click listener for the "수정완료" button
        binding.button4.setOnClickListener {
            // Update the Firestore data with the modified price and sell status
            updateProduct()
        }
    }

    private fun updateProduct() {
        // Get the modified price and sell status
        val modifiedPrice = binding.priceTextView2.text.toString()
        val modifiedSellStatus = binding.sellTextView2.text.toString()

        // Update the product in Firestore using the document ID
        val productRef = firestore?.collection("products")?.document(documentId!!)
        productRef?.update(mapOf(
            "price" to modifiedPrice,
            "sell" to modifiedSellStatus
        ))?.addOnSuccessListener {
            // Handle success (e.g., show a success message)
            // Finish the activity or navigate back to the previous screen
            finish()
        }?.addOnFailureListener {
            // Handle failure (e.g., show an error message)
        }
    }
}

