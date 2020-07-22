package com.policinski.dev.fiteat

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.diegodobelo.expandingview.ExpandingItem
import kotlinx.android.synthetic.main.meal_row_layout.view.*

class MealRecycleListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var mealNutrientsSumList: MutableList<Product> = mutableListOf()
    var allDayMealListProduct: MutableList<MutableList<Product>> = mutableListOf()
    var selectedMeals = ArrayList<String>()

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mealRowTitleMeal = itemView.meal_row_title_meal!!
        private val mealRowKcal = itemView.meal_row_kcal_tv!!
        private val mealRowPro = itemView.meal_row_pro_tv!!
        private val mealRowFat = itemView.meal_row_fat_tv!!
        private val mealRowCarbo = itemView.meal_row_carbo_tv!!
        private val expandableListView = itemView.expanding_list!!
        private val constraintLayout =  itemView.constraintLayout_meal_row!!
        private val myDB = MyDatabaseHelper(itemView.context)
        private var title = "Meal"
        lateinit var item: ExpandingItem


        fun bind(
            product: Product,
            position1: MutableList<Product>,
            position2: String,
            position: Int
        ){
            //set name for meal
            title = position2

            mealRowTitleMeal.text = title
            mealRowKcal.text = "K: " + product.kcal.toString()
            mealRowCarbo.text = "C: %.1f".format(product.carbo).replace(',','.')
            mealRowFat.text = "F: %.1f".format(product.fat).replace(',','.')
            mealRowPro.text = "P: %.1f".format(product.protein).replace(',','.')

            if (expandableListView.itemsCount == 0) { //security created to prevent duplication of the drop-down list view when recycleView is scrolling
                createItem("", position1)
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

                // expand the list by clicking the invisible button -> this button causes a drop down list by clicking anywhere in the view
                itemView.button_expand_list.setOnClickListener{ item.toggleExpanded() }
            }
        }

        fun configureSubItems(
            item: ExpandingItem,
            view: View,
            product: Product
        ) {
            view.setOnClickListener{Toast.makeText(itemView.context, "${product.name}\n ${product.weight}.g", Toast.LENGTH_SHORT).show()}

            view.findViewById<TextView>(R.id.expanding_sub_item_product_name_tv).text = "${product.name}" //Set product name

            view.findViewById<TextView>(R.id.expanding_sub_item_product_nutrients_tv).text = "${product.weight}.g | K: ${product.kcal} | F: ${product.fat} | C: ${product.carbo} | P: ${product.protein}" //Set Nutrients

            view.findViewById<Button>(R.id.expanding_sub_item_delete_product_bt).setOnClickListener{
                item!!.removeSubItem(view)
                myDB.deleteProductFromMeal(product.id)
                refreshActivity()
            }

            (view.findViewById(R.id.expanding_sub_item_edit_product_bt) as Button).setOnClickListener{

                //create dialog for editing product from selected day and meal
                val editDialog = Dialog(itemView.context)
                editDialog.setContentView(R.layout.product_settings_dialog)
                editDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                //find base values of editing product for calculate new nutrients values
                var baseProduct = myDB.findEditedProduct(product.name)

                //init buttons and editText's
                val productName = editDialog.findViewById(R.id.product_edit_name) as TextView
                val butOK = editDialog.findViewById(R.id.ok_product_settings_dialog) as Button
                val butCancel = editDialog.findViewById(R.id.cancel_product_settings_dialog) as Button
                val editKcal = editDialog.findViewById(R.id.kcal_product_settings_dialog) as TextView
                val editPro = editDialog.findViewById(R.id.pro_user_product_settings_dialog) as TextView
                val editFat = editDialog.findViewById(R.id.fat_user_product_settings_dialog) as TextView
                val editCarbo = editDialog.findViewById(R.id.carbo_user_product_settings_dialog) as TextView
                var editWeight = editDialog.findViewById(R.id.product_edit_weight) as EditText

                //set current nutrients from selected product
                productName.text = "${product.name}"
                editKcal.setText("${product.kcal}")
                editPro.setText("${product.protein}")
                editFat.setText("${product.fat}")
                editCarbo.setText("${product.carbo}")
                editWeight.setText("${product.weight}")

                editWeight.addTextChangedListener(object : TextWatcher{
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (editWeight.text.toString().length != 0 && editWeight.text.toString().toInt() > 0) {
                            editKcal.text = ((baseProduct.kcal * editWeight.text.toString()
                                .toInt()) / baseProduct.weight).toString()
                            editPro.text = "%.2f".format(
                                (baseProduct.protein * editWeight.text.toString()
                                    .toDouble()) / baseProduct.weight
                            ).replace(',', '.')
                            editFat.text = "%.2f".format(
                                (baseProduct.fat * editWeight.text.toString()
                                    .toDouble()) / baseProduct.weight
                            ).replace(',', '.')
                            editCarbo.text = "%.2f".format(
                                (baseProduct.carbo * editWeight.text.toString()
                                    .toDouble()) / baseProduct.weight
                            ).replace(',', '.')
                        }else{
                            editKcal.text = "0"
                            editPro.text = "0"
                            editFat.text = "0"
                            editCarbo.text = "0"
                        }
                    }
                })

                //Znajdz produkt po nazwie
                //Odczytaj jego wage
                //zmien jego wage
                //oblicz nowe wartosci
                //zapisz je w bazie
                //zaktualizuj wartosci w liscie

                //save new product parameter
                butOK.setOnClickListener {
                    myDB.editProductNutrients(
                        editKcal.text.toString().toInt(),
                        editPro.text.toString().toDouble(),
                        editFat.text.toString().toDouble(),
                        editCarbo.text.toString().toDouble(),
                        editWeight.text.toString().toInt(),
                        product.id
                    )

                    //set new values for product
                    product.kcal = editKcal.text.toString().toInt()
                    product.protein = editPro.text.toString().toDouble()
                    product.fat = editFat.text.toString().toDouble()
                    product.carbo = editCarbo.text.toString().toDouble()
                    product.weight = editWeight.text.toString().toInt()

                    //set new values in list
                    view.findViewById<TextView>(R.id.expanding_sub_item_product_nutrients_tv).text =
                        "K: ${product.kcal} / F: ${product.fat} / C: ${product.carbo} / P: ${product.protein}" //Set Nutrients

                    //calculate new nutrients values for current meal

                    editDialog.dismiss()

                    refreshActivity()
                }

                //dismiss dialog
                butCancel.setOnClickListener{ editDialog.dismiss() }

                //show dialog
                editDialog.show()

            }
        }

        private fun refreshActivity() {
            val intent = Intent(itemView.context, RefreshtActivity::class.java)
            ContextCompat.startActivity(itemView.context,intent,null)
        }
    }

    fun submitList(
        list: MutableList<Product>,
        mealList: MutableList<MutableList<Product>>,
        readSelectedMealsByUser: Map<Int, String>
    ){
        mealNutrientsSumList = list
        allDayMealListProduct = mealList
        for (title in readSelectedMealsByUser) selectedMeals.add(title.value)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.meal_row_layout,parent,false))
    }

    override fun getItemCount(): Int {
        return mealNutrientsSumList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){

            is ViewHolder -> { holder.bind(mealNutrientsSumList[position],allDayMealListProduct[position],selectedMeals[position],position) }

        }
    }
}