package com.policinski.dev.fiteat.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.policinski.dev.fiteat.MyDatabaseHelper
import com.policinski.dev.fiteat.Product
import com.policinski.dev.fiteat.R
import kotlinx.coroutines.*

class ShoppingListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var itemList = mutableListOf<Product>()

    inner class ViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {

        var productCheckBox: CheckBox = itemView.findViewById(R.id.product_check_box)
        val dataBase = MyDatabaseHelper(itemView.context)

        fun bind(product: Product){
            productCheckBox.text = product.name
            productCheckBox.isChecked = product.favorite == 1

            productCheckBox.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    productCheckBox.setTextColor(Color.LTGRAY)
                    product.favorite = 1
                    dataBase.saveProductStateFromShoppingList(product.id, product.favorite)
                } else {
                    productCheckBox.setTextColor(Color.DKGRAY)
                    product.favorite = 0
                    dataBase.saveProductStateFromShoppingList(product.id, product.favorite)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.shopping_list_product_row,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){

            is ShoppingListAdapter.ViewHolder -> {holder.bind(itemList.get(position))}

        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun submitList(list: MutableList<Product>){
        itemList = list
    }
}