package com.adasoraninda.shoppingcart

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adasoraninda.shoppingcart.adapter.ListProductAdapter
import com.adasoraninda.shoppingcart.adapter.ListProductType
import com.adasoraninda.shoppingcart.databinding.ActivityMainBinding
import com.adasoraninda.shoppingcart.databinding.BottomSheetCartBinding
import com.adasoraninda.shoppingcart.viewmodel.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    private val viewModel: ProductViewModel by viewModels()

    private val listProductAdapter by lazy {
        ListProductAdapter(
            listProductType = ListProductType.UNSELECTED
        )
    }
    private val listSelectedProductAdapter by lazy {
        ListProductAdapter(
            listProductType = ListProductType.SELECTED
        )
    }

    private val layoutBottomSheet: BottomSheetCartBinding? by lazy { binding?.lytBottomSheetCart }
    private val bottomSheet by lazy { BottomSheetBehavior.from(layoutBottomSheet?.root as View) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel.products.observe(this) { products ->
            products?.let {
                listProductAdapter.updateProducts(it)
            }
            products?.let {
                listSelectedProductAdapter.updateProducts(it.filter { product ->
                    product.isSelected == true
                })
            }
        }

        viewModel.pricesOfProducts.observe(this) { price ->
            binding?.txtProductBills?.text = "Rp. ${price ?: 0}"
        }

        setupUI()

        binding?.btnCheckout?.setOnClickListener {
            Toast.makeText(this, "checkout", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI() {
        setupListProduct()
        setupBottomSheet()
    }

    private fun setupListProduct() {
        binding?.lstProduct?.apply {
            layoutManager =
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    GridLayoutManager(this@MainActivity, 2)
                } else {
                    GridLayoutManager(this@MainActivity, 4)
                }
            adapter = listProductAdapter.apply {
                addSelectedQuantity = { id ->
                    viewModel.addSelectedProduct(id)
                    viewModel.incrementQuantityProduct(id)

                    viewModel.updatePrice()
                }
            }
        }

        layoutBottomSheet?.lstSelectedProduct?.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listSelectedProductAdapter.apply {
                incrementProductQuantity = { id ->
                    viewModel.incrementQuantityProduct(id)
                    viewModel.updatePrice()
                }

                decrementProductQuantity = { id ->
                    viewModel.decrementQuantityProduct(id)
                    viewModel.updatePrice()
                }

                deleteProductCallback = { id ->
                    viewModel.deleteSelectedProduct(id)
                    viewModel.updatePrice()
                }
            }
        }
    }

    private fun setupBottomSheet() {
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        binding?.btnCartDetail?.setOnClickListener {
            bottomSheet.state = if (bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED
                || bottomSheet.state == BottomSheetBehavior.STATE_HIDDEN
            ) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }


}