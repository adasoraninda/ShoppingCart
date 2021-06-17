package com.adasoraninda.shoppingcart.viewmodel

import androidx.lifecycle.*
import com.adasoraninda.shoppingcart.model.DummyProduct
import com.adasoraninda.shoppingcart.model.Product

class ProductViewModel : ViewModel() {

    private val mediatorProducts = MediatorLiveData<List<Product>>()
    private val mediatorPrice = MediatorLiveData<Int>()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = mediatorProducts

    val pricesOfProducts: LiveData<Int> get() = mediatorPrice

    init {
        fetchDummyProducts()
        updatePrice()
    }

    private fun fetchDummyProducts() {
        _products.postValue(DummyProduct.getProducts())

        postResource(_products)
    }

    fun addSelectedProduct(productId: Int) {
        val mappingSelected = Transformations.map(_products) { products ->
            products.apply {
                map { product ->
                    if (product.id == productId) {
                        product.isSelected = true
                    }
                }
            }
        }

        postResource(mappingSelected)
    }

    fun deleteSelectedProduct(productId: Int) {
        val mappingUnselected = Transformations.map(_products) { products ->
            products.apply {
                map { product ->
                    if (product.id == productId) {
                        product.resetValue()
                    }
                }
            }
        }

        postResource(mappingUnselected)
    }

    fun decrementQuantityProduct(productId: Int) {
        val mappingDecrement = Transformations.map(_products) { products ->
            products.apply {
                map { product ->
                    if (product.id == productId &&
                        product.selectedQuantity?.compareTo(0) == 1
                    ) {
                        val quantity = product.selectedQuantity?.minus(1) ?: 0

                        if (quantity <= 0) {
                            product.resetValue()
                        } else {
                            product.selectedQuantity = quantity
                            calculatePrice(product)
                        }
                    }
                }
            }
        }

        postResource(mappingDecrement)
    }

    fun incrementQuantityProduct(productId: Int) {
        val mappingIncrement = Transformations.map(_products) { products ->
            products.apply {
                map { product ->
                    if (product.id == productId) {
                        product.selectedQuantity = (product.selectedQuantity ?: 0).plus(1)
                        calculatePrice(product)
                    }
                }
            }
        }

        postResource(mappingIncrement)
    }

    fun updatePrice() {
        val mappingPrice = Transformations.map(_products) { products ->
            products
                .filter { it.isSelected == true }
                .sumOf { it.price ?: 0 }
        }

        mediatorPrice.addSource(mappingPrice) { price ->
            mediatorPrice.postValue(price)
        }
    }

    private fun calculatePrice(product: Product) {
        val realPrice = DummyProduct.getProducts().first { dummyProduct ->
            dummyProduct.id == product.id
        }.price

        product.price = product.selectedQuantity?.let { quantity ->
            realPrice?.times(quantity)
        }
    }

    private fun postResource(liveDataProducts: LiveData<List<Product>>) {
        mediatorProducts.addSource(liveDataProducts) { products ->
            mediatorProducts.postValue(products)
        }
    }


}