package com.policinski.dev.fiteat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.policinski.dev.fiteat.ui.ShoppingListAdapter

class meals_fragment : Fragment() {

    lateinit var shoppingRecycleList: RecyclerView
    lateinit var listAdapter: ShoppingListAdapter
    lateinit var dataBase: MyDatabaseHelper
    lateinit var addProductToShoppingListBt: Button
    lateinit var newProductShoppingList: EditText
    lateinit var deleteSelectedProductFromShoppingListBt: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.shopping_list_fragment, container, false)

        shoppingRecycleList = view.findViewById(R.id.shopping_list_recyclerView)
        addProductToShoppingListBt = view.findViewById(R.id.add_product_to_shopping_list_bt)
        newProductShoppingList = view.findViewById(R.id.name_product_add_shopping_list)
        deleteSelectedProductFromShoppingListBt = view.findViewById(R.id.delete_selected_product_from_shopping_list)
        listAdapter = ShoppingListAdapter()
        dataBase = MyDatabaseHelper(requireContext())
        addProductToShoppingListBt = view.findViewById(R.id.add_product_to_shopping_list_bt)

        shoppingRecycleList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }

        listAdapter.submitList(dataBase.readShoppingList())

        deleteSelectedProductFromShoppingListBt.setOnClickListener { dataBase.deleteSelectedProductFromShoppingList()
            listAdapter.submitList(dataBase.readShoppingList())
            listAdapter.notifyDataSetChanged()
        }

        addProductToShoppingListBt.setOnClickListener { dataBase.addNewProductToShoppingList(newProductShoppingList.text.toString())
            listAdapter.submitList(dataBase.readShoppingList())
            listAdapter.notifyDataSetChanged()
            newProductShoppingList.text.clear()
        }

        return view
    }
}
