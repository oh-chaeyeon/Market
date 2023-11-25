package com.example.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.market.databinding.FragmentListBinding
import com.example.market.databinding.ListItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private var firestore: FirebaseFirestore? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter

    private lateinit var spinner: Spinner
    private var selectedFilter: String = "전체"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the email of the logged-in user
        val loggedInUserEmail: String? = getCurrentLoggedInUserEmail()

        val writeButton = binding.btnWrite
        writeButton.setOnClickListener {
            // Replace with the code to navigate to WriteFragment
            val writeFragment = WriteFragment()

            // Pass the logged-in user email to WriteFragment
            val bundle = Bundle()
            bundle.putString("loggedInUserEmail", loggedInUserEmail)
            writeFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container2, writeFragment)
                .addToBackStack(null)
                .commit()
        }

        firestore = FirebaseFirestore.getInstance()
        recyclerView = binding.recyclerView

        // Pass the user email to the adapter
        adapter = ProductAdapter(loggedInUserEmail) { clickedProduct, documentId ->
            if (loggedInUserEmail == clickedProduct.name) {
                // If the logged-in user is the author, replace with EditFragment
                val editFragment = EditFragment()

                // Pass data to EditFragment
                val bundle = Bundle()
                bundle.putParcelable("productModel", clickedProduct)
                bundle.putString("documentId", documentId)
                editFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container2, editFragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                // If the logged-in user is not the author, replace with DetailFragment
                val detailFragment = DetailFragment()

                // Pass data to DetailFragment
                val bundle = Bundle()
                bundle.putParcelable("productModel", clickedProduct)
                detailFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container2, detailFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Spinner 초기화와 관련된 부분을 ListActivity.kt에서 가져옴
        spinner = binding.spinnerFilter

        // Spinner의 아이템과 어댑터 설정
        val filterArray = resources.getStringArray(R.array.itemList)
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterArray)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        // Spinner의 선택 리스너 설정
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedFilter = filterArray[position]
                // 수정된 함수로 변경
                loadProductsWithFilter()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 작업도 하지 않음
            }
        }

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

    inner class ProductAdapter(
        private val loggedInUserEmail: String?,
        private val click: (Product, String) -> Unit) :
        RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
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

    private fun loadProductsWithFilter() {
        firestore?.collection("products")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                // 오류 처리 (로그, 메시지 표시 등)
                return@addSnapshotListener
            }

            val products = mutableListOf<Pair<Product, String>>()

            for (snapshot in querySnapshot!!.documents) {
                val item = snapshot.toObject(Product::class.java)
                item?.let {
                    val documentId = snapshot.id

                    if (selectedFilter == "전체" || it.sell == selectedFilter) {
                        products.add(Pair(it, documentId))
                    }
                }
            }
            adapter.setProducts(products)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}