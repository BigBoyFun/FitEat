package com.policinski.dev.fiteat

import android.app.Dialog
import android.app.TimePickerDialog
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class MealRecycleListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var mealNutrientsSumList: MutableList<Product> = mutableListOf()
    var allDayMealListProduct: MutableList<MutableList<Product>> = mutableListOf()
    var selectedMeals = ArrayList<String>()
    public var date: String = ""

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val MAIN_PREF = "MAIN_PREF"

        private var sharedPreferences = itemView.context.getSharedPreferences(MAIN_PREF,0)
        private var edit = sharedPreferences.edit()

        private val PREF_BREAKFAST_NOTIFICATION = "PREF_BREAKFAST_NOTIFICATION"
        private val PREF_SECOND_BREAKFAST_NOTIFICATION = "PREF_SECOND_BREAKFAST_NOTIFICATION"
        private val PREF_DINNER_NOTIFICATION = "PREF_DINNER_NOTIFICATION"
        private val PREF_DESSERT_NOTIFICATION = "PREF_DESSERT_NOTIFICATION"
        private val PREF_TEA_NOTIFICATION = "PREF_TEA_NOTIFICATION"
        private val PREF_SUPPER_NOTIFICATION = "PREF_SUPPER_NOTIFICATION"
        private val PREF_SNACKS_NOTIFICATION = "PREF_SNACKS_NOTIFICATION"
        private val PREF_TRAINING_NOTIFICATION = "PREF_TRAINING_NOTIFICATION"

        private val PREF_BREAKFAST_NOTIFICATION_TIME = "PREF_BREAKFAST_NOTIFICATION_TIME"
        private val PREF_SECOND_BREAKFAST_NOTIFICATION_TIME = "PREF_SECOND_BREAKFAST_NOTIFICATION_TIME"
        private val PREF_DINNER_NOTIFICATION_TIME = "PREF_DINNER_NOTIFICATION_TIME"
        private val PREF_DESSERT_NOTIFICATION_TIME = "PREF_DESSERT_NOTIFICATION_TIME"
        private val PREF_TEA_NOTIFICATION_TIME = "PREF_TEA_NOTIFICATION_TIME"
        private val PREF_SUPPER_NOTIFICATION_TIME = "PREF_SUPPER_NOTIFICATION_TIME"
        private val PREF_SNACKS_NOTIFICATION_TIME = "PREF_SNACKS_NOTIFICATION_TIME"
        private val PREF_TRAINING_NOTIFICATION_TIME = "PREF_TRAINING_NOTIFICATION_TIME"

        private val mealRowTitleMeal = itemView.meal_row_title_meal!!
        private val mealRowKcal = itemView.meal_row_kcal_tv!!
        private val mealRowPro = itemView.meal_row_pro_tv!!
        private val mealRowFat = itemView.meal_row_fat_tv!!
        private val mealRowCarbo = itemView.meal_row_carbo_tv!!
        private val expandableListView = itemView.expanding_list!!
        private val constraintLayout =  itemView.constraintLayout_meal_row!!
        private val myDB = MyDatabaseHelper(itemView.context)
        private var mealTitle = "Meal"
        private var notificationState =itemView.notification_meal_state_but
        private var mealTimePicker = itemView.meal_time_tv
        private var state: Boolean = false
        lateinit var item: ExpandingItem


        fun bind(
            productMeal: Product,
            position1: MutableList<Product>,
            position2: String,
            position: Int,
            date: String
        ){
            //set name for meal
            mealTitle = position2
            readMealNotificationState(position2)

            mealRowTitleMeal.text = mealTitle
            mealRowKcal.text = "K: " + productMeal.kcal.toString()
            mealRowCarbo.text = "C: %.1f".format(productMeal.carbo).replace(',','.')
            mealRowFat.text = "F: %.1f".format(productMeal.fat).replace(',','.')
            mealRowPro.text = "P: %.1f".format(productMeal.protein).replace(',','.')

            //set visible if date is today, else set invisible(cant change past and future )
            notificationState.visibility = when(date){ LocalDate.now().toString() -> View.VISIBLE else -> View.GONE}
            mealTimePicker.visibility = when(date){ LocalDate.now().toString() -> View.VISIBLE else -> View.GONE}

            notificationState.setOnClickListener {
                when(state){
                    true -> it.setBackgroundResource(R.drawable.ic_baseline_notifications_24)
                    false -> it.setBackgroundResource(R.drawable.ic_baseline_notifications_active_24)
                }

                state = when(state){
                    true -> false
                    false -> true
                }

                mealTimePicker.isEnabled = state

                when(state){
                    true -> saveMealNotificationState(state, position2)
                    false -> saveMealNotificationState(state, position2)
                }

            }

            //open timePickerDialog by click on hour next to notification bell
            mealTimePicker.setOnClickListener {v ->

                v as TextView
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    cal.set(Calendar.MINUTE, minute)

                    //set time to the notificationTime TextView
                    mealTimePicker.text = SimpleDateFormat("HH:mm").format(cal.time)

                    //save meal time notification do memory
                    saveNotificationTIme(mealTitle,SimpleDateFormat("HH:mm").format(cal.time))
                }

                TimePickerDialog(itemView.context,timeSetListener,cal.get(Calendar.HOUR_OF_DAY), cal.get(
                    Calendar.MINUTE),true).show()

            }

            if (expandableListView.itemsCount == 0) { //security created to prevent duplication of the drop-down list view when recycleView is scrolling
                createItem("", position1,date,productMeal, mealTitle)
            }


        }

        private fun saveMealNotificationState(state: Boolean, title: String) {

            when(title){
                "Breakfast" -> edit.putBoolean(PREF_BREAKFAST_NOTIFICATION,state).apply()
                "Second breakfast" -> edit.putBoolean(PREF_SECOND_BREAKFAST_NOTIFICATION,state).apply()
                "Dinner" -> edit.putBoolean(PREF_DINNER_NOTIFICATION,state).apply()
                "Dessert" -> edit.putBoolean(PREF_DESSERT_NOTIFICATION,state).apply()
                "Tea" -> edit.putBoolean(PREF_TEA_NOTIFICATION,state).apply()
                "Supper" -> edit.putBoolean(PREF_SUPPER_NOTIFICATION,state).apply()
                "Snacks" -> edit.putBoolean(PREF_SNACKS_NOTIFICATION,state).apply()
                "Training" -> edit.putBoolean(PREF_TRAINING_NOTIFICATION,state).apply()
            }

        }

        private fun saveNotificationTIme(title: String, time: String){

            when(title){
                "Breakfast" -> edit.putString(PREF_BREAKFAST_NOTIFICATION_TIME,time).apply()
                "Second breakfast" -> edit.putString(PREF_SECOND_BREAKFAST_NOTIFICATION_TIME,time).apply()
                "Dinner" -> edit.putString(PREF_DINNER_NOTIFICATION_TIME,time).apply()
                "Dessert" -> edit.putString(PREF_DESSERT_NOTIFICATION_TIME,time).apply()
                "Tea" -> edit.putString(PREF_TEA_NOTIFICATION_TIME,time).apply()
                "Supper" -> edit.putString(PREF_SUPPER_NOTIFICATION_TIME,time).apply()
                "Snacks" -> edit.putString(PREF_SNACKS_NOTIFICATION_TIME,time).apply()
                "Training" -> edit.putString(PREF_TRAINING_NOTIFICATION_TIME,time).apply()
            }

        }

        private fun readMealNotificationState(title: String){

            when(title){
                "Breakfast" -> state = sharedPreferences.getBoolean(PREF_BREAKFAST_NOTIFICATION,false)
                "Second breakfast" -> state = sharedPreferences.getBoolean(PREF_SECOND_BREAKFAST_NOTIFICATION,false)
                "Dinner" -> state = sharedPreferences.getBoolean(PREF_DINNER_NOTIFICATION,false)
                "Dessert" -> state = sharedPreferences.getBoolean(PREF_DESSERT_NOTIFICATION,false)
                "Tea" -> state = sharedPreferences.getBoolean(PREF_TEA_NOTIFICATION,false)
                "Supper" -> state = sharedPreferences.getBoolean(PREF_SUPPER_NOTIFICATION,false)
                "Snacks" -> state = sharedPreferences.getBoolean(PREF_SNACKS_NOTIFICATION,false)
                "Training" -> state = sharedPreferences.getBoolean(PREF_TRAINING_NOTIFICATION,false)
            }

            when(title){
                "Breakfast" -> mealTimePicker.text = sharedPreferences.getString(PREF_BREAKFAST_NOTIFICATION_TIME,"00:00")
                "Second breakfast" -> mealTimePicker.text = sharedPreferences.getString(PREF_SECOND_BREAKFAST_NOTIFICATION_TIME,"00:00")
                "Dinner" -> mealTimePicker.text = sharedPreferences.getString(PREF_DINNER_NOTIFICATION_TIME,"00:00")
                "Dessert" -> mealTimePicker.text = sharedPreferences.getString(PREF_DESSERT_NOTIFICATION_TIME,"00:00")
                "Tea" -> mealTimePicker.text = sharedPreferences.getString(PREF_TEA_NOTIFICATION_TIME,"00:00")
                "Supper" -> mealTimePicker.text = sharedPreferences.getString(PREF_SUPPER_NOTIFICATION_TIME,"00:00")
                "Snacks" -> mealTimePicker.text = sharedPreferences.getString(PREF_SNACKS_NOTIFICATION_TIME,"00:00")
                "Training" -> mealTimePicker.text = sharedPreferences.getString(PREF_TRAINING_NOTIFICATION_TIME,"00:00")
            }

            when(state){
                true -> notificationState.setBackgroundResource(R.drawable.ic_baseline_notifications_active_24)
                false -> notificationState.setBackgroundResource(R.drawable.ic_baseline_notifications_24)
            }

            mealTimePicker.isEnabled = state

        }

        fun createItem(
            title: String,
            productList: MutableList<Product>,
            date: String,
            productMeal: Product,
            mealTitle: String
        ){
            addItem(title,productList,R.color.colorAccent,R.drawable.ic_restaurant_menu_white_24dp,date,productMeal,mealTitle)
        }

        fun addItem(
            title: String,
            subItem: MutableList<Product>,
            colorRes: Int,
            iconRes: Int,
            date: String,
            productMeal: Product,
            mealTitle: String
        ){

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
                    configureSubItems(item,view,subItem[i],date,productMeal,mealTitle)
                }

                // expand the list by clicking the invisible button -> this button causes a drop down list by clicking anywhere in the view
                itemView.button_expand_list.setOnClickListener{ item.toggleExpanded() }
            }
        }

        fun configureSubItems(
            item: ExpandingItem,
            view: View,
            product: Product,
            date: String,
            productMeal: Product,
            mealTitle: String
        ) {
            view.setOnClickListener{Toast.makeText(itemView.context, "${product.name}\n ${product.weight}.g", Toast.LENGTH_SHORT).show()}

            view.findViewById<TextView>(R.id.expanding_sub_item_product_name_tv).text = "${product.name} " //Set product name

            view.findViewById<TextView>(R.id.expanding_sub_item_product_nutrients_tv).text = "${product.weight}.g | K: ${product.kcal} | F: ${product.fat} | C: ${product.carbo} | P: ${product.protein}" //Set Nutrients

            //set visibility for edit and delete buttons when selected date is today else buttons are gone -> user cant change history
            (view.findViewById(R.id.expanding_sub_item_edit_product_bt) as Button).visibility = when(date){ LocalDate.now().toString() -> View.VISIBLE else -> View.GONE}


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
                val butDelete = editDialog.findViewById(R.id.cancel_product_settings_dialog) as Button
                val editKcal = editDialog.findViewById(R.id.kcal_product_settings_dialog) as TextView
                val editPro = editDialog.findViewById(R.id.pro_user_product_settings_dialog) as TextView
                val editFat = editDialog.findViewById(R.id.fat_user_product_settings_dialog) as TextView
                val editCarbo = editDialog.findViewById(R.id.carbo_user_product_settings_dialog) as TextView
                var editWeight = editDialog.findViewById(R.id.product_edit_weight) as EditText

                //set current nutrients from selected product
                productName.text = "${product.name}"
                editKcal.text = "${product.kcal}"
                editPro.text = "${product.protein}"
                editFat.text = "${product.fat}"
                editCarbo.text = "${product.carbo}"
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

                    refreshNutrientsValuesInExpandingLayoutTitle(date)

                    //set new values for product
                    product.kcal = editKcal.text.toString().toInt()
                    product.protein = editPro.text.toString().toDouble()
                    product.fat = editFat.text.toString().toDouble()
                    product.carbo = editCarbo.text.toString().toDouble()
                    product.weight = editWeight.text.toString().toInt()



                    //set new values in list
                    view.findViewById<TextView>(R.id.expanding_sub_item_product_nutrients_tv).text =
                        "${product.weight}.g | K: ${product.kcal} | F: ${product.fat} | C: ${product.carbo} | P: ${product.protein}" //Set Nutrients


                    editDialog.dismiss()

                    refreshActivity()
                }

                //delete product from current day
                butDelete.setOnClickListener{

                    //Delete product drom expanding list
                    item!!.removeSubItem(view)

                    //Delete product from day table in database
                    myDB.deleteProductFromMeal(product.id)

                    //refresh Nutrients Values In Expanding Layout Title
                    refreshNutrientsValuesInExpandingLayoutTitle(date)

                    editDialog.dismiss()

                    refreshActivity()

                }

                //show dialog
                editDialog.show()

            }
        }

        private fun refreshNutrientsValuesInExpandingLayoutTitle(date: String){

            var calculatedProduct: Product = Product()

            val db = MyDatabaseHelper(itemView.context)
            val cursor = db.readMealFromDay(date, findMealByTitle(mealTitle))

            for (item in cursor){
                calculatedProduct.kcal += item.kcal
                calculatedProduct.carbo += item.carbo
                calculatedProduct.fat += item.fat
                calculatedProduct.protein += item.protein
                calculatedProduct.weight += item.weight
            }

            mealRowKcal.text = "K: " + calculatedProduct.kcal.toString()
            mealRowCarbo.text = "C: %.1f".format(calculatedProduct.carbo).replace(',','.')
            mealRowFat.text = "F: %.1f".format(calculatedProduct.fat).replace(',','.')
            mealRowPro.text = "P: %.1f".format(calculatedProduct.protein).replace(',','.')
        }

        private fun refreshActivity() {
            val intent = Intent(itemView.context, RefreshtActivity::class.java)
            ContextCompat.startActivity(itemView.context,intent,null)
        }

        private fun findMealByTitle(title: String): Int{

            return when(title){
                "Breakfast" -> 1
                "Second breakfast" -> 2
                "Dinner" -> 3
                "Dessert" -> 4
                "Tea" -> 5
                "Supper" -> 6
                "Snacks" -> 7
                "Training" -> 8
                else -> 1
            }

        }

    }

    fun submitList(
        list: MutableList<Product>,
        mealList: MutableList<MutableList<Product>>,
        readSelectedMealsByUser: Map<Int, String>,
        selectedDate: String
    ){
        date = selectedDate
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

            is ViewHolder -> { holder.bind(mealNutrientsSumList[position],allDayMealListProduct[position],selectedMeals[position],position,date) }

        }
    }
}