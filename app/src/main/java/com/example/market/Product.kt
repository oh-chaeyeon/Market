package com.example.market

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class Product(
    @SerializedName("title")val title: String = "",
    @SerializedName("price")val price: String = "",
    @SerializedName("image")val imageUrl: String = "",
    @SerializedName("content") val content: String = ""
): Parcelable