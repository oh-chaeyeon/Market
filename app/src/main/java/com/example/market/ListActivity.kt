package com.example.market


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.market.databinding.ActivityListBinding
import com.example.market.databinding.ListItemBinding
import com.google.firebase.auth.FirebaseAuth
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

        // Get the email of the logged-in user
        val loggedInUserEmail: String? = getCurrentLoggedInUserEmail()

        val writeButton=findViewById<Button>(R.id.btnWrite)
        writeButton.setOnClickListener {
            val intent = Intent(this, WriteActivity::class.java)
            startActivity(intent)
        }

        firestore = FirebaseFirestore.getInstance()
        recyclerView = binding.recyclerView

        // Pass the user email to the adapter
        adapter = ProductAdapter(loggedInUserEmail) { clickedProduct, documentId ->
            if (loggedInUserEmail == clickedProduct.name) {
                // If the logged-in user is the author, go to EditActivity
                val intent = Intent(this@ListActivity, EditActivity::class.java)
                intent.putExtra("productModel", clickedProduct)
                intent.putExtra("documentId", documentId)
                this@ListActivity.startActivity(intent)
            } else {
                // If the logged-in user is not the author, go to DetailActivity
                val intent = Intent(this@ListActivity, DetailActivity::class.java)
                intent.putExtra("productModel", clickedProduct)
                this@ListActivity.startActivity(intent)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadProducts()
    }

    private fun getCurrentLoggedInUserEmail(): String? {
        return FirebaseAuth.getInstance().currentUser?.email
    }



    private fun loadProducts() {
        firestore?.collection("products")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                // Handle error (log, show a message, etc.)
                return@addSnapshotListener
            }

            val products = mutableListOf<Pair<Product, String>>()

            for (snapshot in querySnapshot!!.documents) {
                val item = snapshot.toObject(Product::class.java)
                item?.let {
                    val documentId = snapshot.id
                    products.add(Pair(it, documentId))
                }
            }
            adapter.setProducts(products)
        }
    }

    inner class ProductAdapter(private val loggedInUserEmail: String?, private val click: (Product, String) -> Unit) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
        private var products: List<Pair<Product, String>> = emptyList()

        fun setProducts(products: List<Pair<Product, String>>) {
            this.products = products
            notifyDataSetChanged()
        }

        inner class ViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                itemView.setOnClickListener {
                    // Handle item click
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val (clickedProduct, documentId) = products[position]
                        click.invoke(clickedProduct, documentId)
                    }
                }
            }

            fun bind(product: Product) {
                // 이미지 로딩
                Glide.with(binding.root.context)
                    .load(product.imageUrl)
                    .into(binding.imageView)

                binding.titleTextView.text = product.title
                binding.priceTextView.text = product.price + "원"
                binding.sellTextView.text = product.sell
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(products[position].first)
        }

        override fun getItemCount(): Int {
            return products.size
        }
    }
}

