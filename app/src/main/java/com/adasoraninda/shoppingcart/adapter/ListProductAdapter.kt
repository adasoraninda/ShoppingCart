package com.adasoraninda.shoppingcart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.adasoraninda.shoppingcart.R
import com.adasoraninda.shoppingcart.databinding.ListItemProductBinding
import com.adasoraninda.shoppingcart.databinding.ListItemSelectedProductBinding
import com.adasoraninda.shoppingcart.model.Product

class ListProductAdapter(private val listProductType: ListProductType) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val products = arrayListOf<Product>()

    fun updateProducts(products: List<Product>) {
        this.products.clear()
        this.products.addAll(products)

        notifyDataSetChanged()
    }

    var addSelectedQuantity: ((productId: Int) -> Unit)? = null

    var incrementProductQuantity: ((productId: Int) -> Unit)? = null
    var decrementProductQuantity: ((productId: Int) -> Unit)? = null
    var deleteProductCallback: ((productId: Int) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return when (listProductType) {
            ListProductType.UNSELECTED -> R.layout.list_item_product
            ListProductType.SELECTED -> R.layout.list_item_selected_product
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.list_item_product -> ProductViewHolder(
                ListItemProductBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
            R.layout.list_item_selected_product -> ProductSelectedViewHolder(
                (ListItemSelectedProductBinding.inflate(
                    inflater, parent, false
                ))
            )
            else -> throw IllegalArgumentException("Unknown list type")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (listProductType) {
            ListProductType.UNSELECTED -> {
                (holder as ProductViewHolder).bind(products[position])
            }
            ListProductType.SELECTED -> {
                (holder as ProductSelectedViewHolder).bind(products[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }


    inner class ProductViewHolder(private val binding: ListItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            with(binding) {
                txtProductName.text = product.name
                txtProductPrice.text = product.getRealPrice().toString()
                imgProduct.load(product.image)

                root.setOnClickListener {
                    product.id?.let { id -> addSelectedQuantity?.invoke(id) }
                }
            }
        }
    }

    inner class ProductSelectedViewHolder(private val binding: ListItemSelectedProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            with(binding) {
                txtProductName.text = product.name
                txtProductPrice.text = (product.price ?: 0).toString()
                imgProduct.load(product.image)
                lytNumberPicker.txtProductQuantity.text = (product.selectedQuantity ?: 0).toString()

                lytNumberPicker.imgIncrementQuantity.setOnClickListener {
                    product.id?.let { id -> incrementProductQuantity?.invoke(id) }
                }

                lytNumberPicker.imgDecrementQuantity.setOnClickListener {
                    product.id?.let { id -> decrementProductQuantity?.invoke(id) }
                }

                btnMore.setOnClickListener {
                    PopupMenu(it.context, it).apply {
                        menuInflater.inflate(R.menu.popup_menu, menu)
                        setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.action_delete -> {
                                    product.id?.let { id -> deleteProductCallback?.invoke(id) }
                                    true
                                }
                                else -> true
                            }
                        }
                    }.show()
                }
            }
        }

    }
}

enum class ListProductType {
    UNSELECTED, SELECTED
}