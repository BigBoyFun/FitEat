package com.policinski.dev.fiteat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.diegodobelo.expandingview.ExpandingItem
import kotlinx.android.synthetic.main.meal_row_layout.view.*
import java.util.zip.Inflater

class MealRecycleListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var mealNutrientsSumList: MutableList<Product> = mutableListOf()
    var allDayMealListProduckt: MutableList<MutableList<Product>> = mutableListOf()

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){

        val mealRowTitleMeal = itemView.meal_row_title_meal
        val mealRowKcal = itemView.meal_row_kcal_tv
        val mealRowPro = itemView.meal_row_pro_tv
        val mealRowFat = itemView.meal_row_fat_tv
        val mealRowCarbo = itemView.meal_row_carbo_tv
        val expandableListView = itemView.expanding_list
        val myDB = MyDatabaseHelper(itemView.context)
        var title = "Meal"
        lateinit var item: ExpandingItem


        fun bind(
            product: Product,
            position1: MutableList<Product>,
            position: Int
        ){

            //set name for meal
            when(position){
                0 -> title = "1.Breakfast"
                1 -> title = "2.Second breakfast"
                2 -> title = "3.Dinner"
                3 -> title = "4.Dessert"
                4 -> title = "5.Tea"
                5 -> title = "6.Supper"
                6 -> title = "7.Snacks"
                7 -> title = "8.Training"
            }

            mealRowTitleMeal.text = title
            mealRowKcal.text = product.kcal.toString()
            mealRowCarbo.text = "%.2f".format(product.carbo).replace(',','.')
            mealRowFat.text = "%.2f".format(product.fat).replace(',','.')
            mealRowPro.text = "%.2f".format(product.protein).replace(',','.')

            if (expandableListView.itemsCount == 0) { //security created to prevent duplication of the drop-down list view when recycleView is scrolling
                createItem("DETAILS", position1)
            }

        }

        fun createItem(title: String, productList: MutableList<Product>){
            addItem(title,productList,R.color.colorAccent,R.drawable.ic_restaurant_menu_white_24dp)
        }

        fun addItem(title: String, subItem: MutableList<Product>, colorRes: Int, iconRes: Int){

            //Create item with R.layout.expanding_layout
            item = expandableListView.createNewItem(R.layout.expanding_layout)

            //If item creation is successful. let's configure it
            if (item != null){
                item.setIndicatorColorRes(colorRes)
                item.setIndicatorIconRes(iconRes)
                //It is possible to get any view inside the inflated layout. Let's set the text in the item
                (item.findViewById(R.id.expand_list_title_tv) as TextView).text = title

                //We can create item in batch
                item.createSubItems(subItem.size)
                for (i in 0 until item.subItemsCount){
                    //Create sum item by its index
                    val view = item.getSubItemView(i)

                    //create some values in
                    configureSubItems(item,view,subItem[i])
                }
            }
        }

        fun configureSubItems(
            item: ExpandingItem,
            view: View,
            product: Product
        ) {
            view.setOnClickListener{Toast.makeText(itemView.context, "${product.name}", Toast.LENGTH_SHORT).show()}

            view.findViewById<TextView>(R.id.expanding_sub_item_product_name_tv).text = product.name //Set product name

            view.findViewById<TextView>(R.id.expanding_sub_item_product_nutrients_tv).text = "K: ${product.kcal} / F: ${product.fat} / C: ${product.carbo} / P: ${product.protein}" //Set Nutrients

            view.findViewById<Button>(R.id.expanding_sub_item_delete_product_bt).setOnClickListener{
                item!!.removeSubItem(view)
                myDB.deleteProductFromMeal(product.id)
            }

            (view.findViewById(R.id.expanding_sub_item_edit_product_bt) as Button).setOnClickListener{
                Toast.makeText(view.context, "Edit", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun submitList(
        list: MutableList<Product>,
        mealList: MutableList<MutableList<Product>>
    ){
        mealNutrientsSumList = list
        allDayMealListProduckt = mealList

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.meal_row_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return mealNutrientsSumList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){

            is ViewHolder -> { holder.bind(mealNutrientsSumList[position],allDayMealListProduckt[position],position) }

        }
    }
}