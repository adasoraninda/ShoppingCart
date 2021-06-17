package com.adasoraninda.shoppingcart.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    var id: Int? = null,
    var name: String? = null,
    var price: Int? = null,
    var image: String? = null,
    var selectedQuantity: Int? = null,
    var isSelected: Boolean? = null
) : Parcelable {
    fun resetValue() = apply {
        isSelected = false
        price = getRealPrice()
        selectedQuantity = 0
    }

    fun getRealPrice(): Int {
        return DummyProduct.getProducts()
            .first { dummyProduct -> dummyProduct.id == id }
            .price ?: 0
    }
}

object DummyProduct {
    fun getProducts() = listOf(
        Product(
            id = 1,
            name = "Beng beng",
            price = 3000,
            image = "https://pbs.twimg.com/profile_images/1210618202457292802/lt9KD2lt.jpg",
        ),
        Product(
            id = 2,
            name = "Indomie",
            price = 2000,
            image = "https://pbs.twimg.com/profile_images/1210618202457292802/lt9KD2lt.jpg",
        ),
        Product(
            id = 3,
            name = "Bakso",
            price = 1000,
            image = "https://pbs.twimg.com/profile_images/1210618202457292802/lt9KD2lt.jpg",
        ),
        Product(
            id = 4,
            name = "Ciki",
            price = 1000,
            image = "https://pbs.twimg.com/profile_images/1210618202457292802/lt9KD2lt.jpg",
        ),
        Product(
            id = 5,
            name = "Taro",
            price = 3000,
            image = "https://pbs.twimg.com/profile_images/1210618202457292802/lt9KD2lt.jpg",
        ),
        Product(
            id = 6,
            name = "Tango",
            price = 2000,
            image = "https://pbs.twimg.com/profile_images/1210618202457292802/lt9KD2lt.jpg",
        ),
    )
}