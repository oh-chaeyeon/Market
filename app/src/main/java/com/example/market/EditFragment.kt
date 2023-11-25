package com.example.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.market.databinding.FragmentEditBinding
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class EditFragment : Fragment() {
    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private var firestore: FirebaseFirestore? = null
    private var product: Product? = null
    private var documentId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container2: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container2, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        product = arguments?.getParcelable("productModel")
        documentId = arguments?.getString("documentId")

        binding.priceTextView2.hint = product?.price
        binding.sellSwitch.isChecked = product?.sell == "판매완료"

        binding.button4.setOnClickListener {
            updateProduct()
        }
    }

    private fun updateProduct() {
        var modifiedPrice = binding.priceTextView2.text.toString()
        val modifiedSellStatus = if (binding.sellSwitch.isChecked) "판매완료" else "판매중"

        val productRef = firestore?.collection("products")?.document(documentId!!)
        productRef?.update(
            mapOf(
                "price" to modifiedPrice,
                "sell" to modifiedSellStatus
            )
        )?.addOnSuccessListener {
            activity?.supportFragmentManager?.popBackStack()
        }?.addOnFailureListener {
            // Handle failure (e.g., show an error message)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
