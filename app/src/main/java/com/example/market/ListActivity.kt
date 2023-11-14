package com.example.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.market.databinding.ActivityListBinding
import com.example.market.databinding.ListItemBinding
import com.google.firebase.firestore.FirebaseFirestore

class ListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListBinding
    private var firestore: FirebaseFirestore? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        recyclerView = binding.recyclerView
        adapter = ProductAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadProducts()
    }

    private fun loadProducts() {
        firestore?.collection("products")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                // Handle error (log, show a message, etc.)
                return@addSnapshotListener
            }

            val products = mutableListOf<Product>()

            for (snapshot in querySnapshot!!.documents) {
                val item = snapshot.toObject(Product::class.java)
                item?.let {
                    products.add(it)
                }
            }
            adapter.setProducts(products)
        }
    }

    inner class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
        private var products: List<Product> = emptyList()

        fun setProducts(products: List<Product>) {
            this.products = products
            notifyDataSetChanged()
        }

        inner class ViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                itemView.setOnClickListener {
                    // Handle item click
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        // Do something with the clicked item
                    }
                }
            }

            fun bind(product: Product) {
                // 이미지 로딩
                Glide.with(binding.root.context)
                    .load(product.imageUrl)
                    .into(binding.imageView)

                binding.titleTextView.text = product.title
                binding.priceTextView.text = product.price
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(products[position])
        }

        override fun getItemCount(): Int {
            return products.size
        }
    }
}







