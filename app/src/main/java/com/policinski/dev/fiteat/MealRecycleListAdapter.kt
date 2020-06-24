package com.policinski.dev.fiteat

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

                //create dialog for editing product from selected day and meal
                val editDialog = Dialog(itemView.context)
                editDialog.setContentView(R.layout.product_settings_dialog)
                editDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                //init buttons and editText's
                val butOK = editDialog.findViewById(R.id.ok_product_settings_dialog) as Button
                val butCancel = editDialog.findViewById(R.id.cancel_product_settings_dialog) as Button
                val editKcal = editDialog.findViewById(R.id.kcal_product_settings_dialog) as EditText
                val editPro = editDialog.findViewById(R.id.pro_user_product_settings_dialog) as EditText
                val editFat = editDialog.findViewById(R.id.fat_user_product_settings_dialog) as EditText
                val editCarbo = editDialog.findViewById(R.id.carbo_user_product_settings_dialog) as EditText

                //set current nutrients from selected product
                editKcal.setText("${product.kcal}")
                editPro.setText("${product.protein}")
                editFat.setText("${product.fat}")
                editCarbo.setText("${product.carbo}")

                //save new product parameter
                butOK.setOnClickListener {
                    myDB.editProductNutrients(
                        editKcal.text.toString().toInt(),
                        editPro.text.toString().toInt(),
                        editFat.text.toString().toInt(),
                        editCarbo.text.toString().toInt(),
                        product.id
                    )

                    editDialog.dismiss()
                }

                //dismiss dialog
                butCancel.setOnClickListener{ editDialog.dismiss() }

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