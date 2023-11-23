package com.example.market

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.market.databinding.FragmentDetailBinding

@Suppress("DEPRECATION")
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 여기에서 데이터를 받아와서 UI 업데이트 수행
        val product = arguments?.getParcelable<Product>("productModel")

        binding.titleTextView.text = product?.title.orEmpty()
        binding.priceTextView.text = product?.price.orEmpty() + "원"
        binding.contentTextView.text = product?.content.orEmpty()
        binding.sellTextView.text = product?.sell.orEmpty()

        Glide.with(requireContext())
            .load(product?.imageUrl)
            .into(binding.imageView3)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
