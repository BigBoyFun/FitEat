package com.policinski.dev.fiteat

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.solver.widgets.Rectangle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_products_fragment.*
import kotlinx.android.synthetic.main.fragment_products_fragment.view.but_filter_all_products
import kotlinx.android.synthetic.main.fragment_products_fragment.view.but_filter_breakfast
import kotlinx.android.synthetic.main.fragment_products_fragment.view.but_filter_dessert
import kotlinx.android.synthetic.main.fragment_products_fragment.view.but_filter_dinner
import kotlinx.android.synthetic.main.fragment_products_fragment.view.but_filter_secondbreakfast
import kotlinx.android.synthetic.main.fragment_products_fragment.view.but_filter_snacks
import kotlinx.android.synthetic.main.fragment_products_fragment.view.but_filter_supper
import kotlinx.android.synthetic.main.fragment_products_fragment.view.but_filter_tea
import kotlinx.android.synthetic.main.fragment_products_fragment.view.but_filter_training
import kotlinx.android.synthetic.main.fragment_products_fragment.view.currently_viewed_list_of_product
import kotlinx.android.synthetic.main.fragment_products_fragment.view.floatingActionButton
import kotlinx.android.synthetic.main.fragment_products_fragment.view.products_recycle_view
import kotlinx.android.synthetic.main.fragment_products_fragment.view.searchView
import kotlinx.android.synthetic.main.fragment_products_fragment.view.text_meal_name
import kotlinx.android.synthetic.main.fragment_products_fragment_copy.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.properties.Delegates

class ProductsFragment : Fragment(), SearchView.OnQueryTextListener, View.OnClickListener{

    private lateinit var adapterProduct: MyAdapter
    private lateinit var productList: MutableList<Product>
    private lateinit var dbManager: MyDatabaseHelper
    private lateinit var buttonFilterList: List<Button>
    private val MAIN_PREF = "MAIN_PREF"
    private val PREF_CARBO = "PREF_CARBO"
    private val PREF_FAT = "PREF_FAT"
    private val PREF_KCAL = "PREF_KCAL"
    private val PREF_PRO = "PREF_PRO"

    private val PREF_BREAKFAST_NOTIFICATION_TIME = "PREF_BREAKFAST_NOTIFICATION_TIME"
    private val PREF_SECOND_BREAKFAST_NOTIFICATION_TIME = "PREF_SECOND_BREAKFAST_NOTIFICATION_TIME"
    private val PREF_DINNER_NOTIFICATION_TIME = "PREF_DINNER_NOTIFICATION_TIME"
    private val PREF_DESSERT_NOTIFICATION_TIME = "PREF_DESSERT_NOTIFICATION_TIME"
    private val PREF_TEA_NOTIFICATION_TIME = "PREF_TEA_NOTIFICATION_TIME"
    private val PREF_SUPPER_NOTIFICATION_TIME = "PREF_SUPPER_NOTIFICATION_TIME"
    private val PREF_SNACKS_NOTIFICATION_TIME = "PREF_SNACKS_NOTIFICATION_TIME"
    private val PREF_TRAINING_NOTIFICATION_TIME = "PREF_TRAINING_NOTIFICATION_TIME"

    private val PREF_BREAKFAST_NOTIFICATION = "PREF_BREAKFAST_NOTIFICATION"
    private val PREF_SECOND_BREAKFAST_NOTIFICATION = "PREF_SECOND_BREAKFAST_NOTIFICATION"
    private val PREF_DINNER_NOTIFICATION = "PREF_DINNER_NOTIFICATION"
    private val PREF_DESSERT_NOTIFICATION = "PREF_DESSERT_NOTIFICATION"
    private val PREF_TEA_NOTIFICATION = "PREF_TEA_NOTIFICATION"
    private val PREF_SUPPER_NOTIFICATION = "PREF_SUPPER_NOTIFICATION"
    private val PREF_SNACKS_NOTIFICATION = "PREF_SNACKS_NOTIFICATION"
    private val PREF_TRAINING_NOTIFICATION = "PREF_TRAINING_NOTIFICATION"

    private val PREF_AUTO_SUGGEST_MEAL = "PREF_AUTO_SUGEST_MEAL"
    private val PREF_CURRENTLY_VIEWED_LIST = "PREF_CURRENTLY_VIEWED_LIST" //created to show the correct meal list and correctly select the filter button
    private val PREF_DELETE_PRODUCT_FROM_LIST = "PREF_DELETE_PRODUCT_FROM_LIST" //created for deleting product from specific meal(not from DB)

    private var current_kcal = 0
    private var current_pro = 0.0
    private var current_fat = 0.0
    private var current_carbo = 0.0
    private var autoSuggestMeal by Delegates.notNull<Boolean>()
    private var meal: Int = 0
    lateinit var date: Any
    lateinit var mealScrollView: HorizontalScrollView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {

        }

        //read from sharedPref information about current meal that should be updated
        meal = readNotificationAlertTime()

        productList = mutableListOf() //initialization of the list that will contain products

        autoSuggestMeal = this.requireContext().getSharedPreferences(MAIN_PREF,0).getBoolean(PREF_AUTO_SUGGEST_MEAL,true)

        this.requireContext().getSharedPreferences(MAIN_PREF,0).edit().putString(PREF_CURRENTLY_VIEWED_LIST,"All").apply() //necessarily for correctly refresh fragment when user click on button "Products" in bottom menu
        loadingProductListWithAutoSuggest()
        this.requireContext().getSharedPreferences(MAIN_PREF,0).edit().putString(PREF_CURRENTLY_VIEWED_LIST,findMealByNum(meal)).apply() //necessarily for correctly refresh fragment when user click on button "Products" in bottom menu

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_products_fragment_copy, container, false)

        //read any product added to current day and calculate all nutrients
        val nutrientsSumArray = dbManager.readDayTable(date.toString())

        view.day_sum_kcal.text = nutrientsSumArray[0].toInt().toString()
        view.day_sum_fat.text = "%.1f".format(nutrientsSumArray[1]).replace(',','.')
        view.day_sum_carbo.text = "%.1f".format(nutrientsSumArray[2]).replace(',','.')
        view.day_sum_pro.text = "%.1f".format(nutrientsSumArray[3]).replace(',','.')

        //init scrollView with meal list name
        mealScrollView = view.findViewById(R.id.horizontalScrollView2)

        //show activity in you can add new product to data base
        view.floatingActionButton.setOnClickListener {
            //createProductDialog()
            val intent: Intent = Intent(requireContext(), newProductActivity::class.java) //FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK
            startActivityForResult(intent,100,null)
//            startActivity(intent)

            }

        //searching product recycle list view
        view.searchView.setOnQueryTextListener(this)

        view.products_recycle_view.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapterProduct = MyAdapter()
            adapter = adapterProduct
        }

        adapterProduct.submitList(productList)

        val kcalUserPref = view.home_user_pref_kcal
        val fatUserPref = view.home_user_pref_fat
        val proUserPref = view.home_user_pref_carbo
        val carboUserPref = view.home_user_pref_pro

        readData(kcalUserPref,fatUserPref,proUserPref,carboUserPref)

        //initialization of buttons responsible for specifying the list of products in terms of a meal
        val butAll = view.but_filter_all_products
        val but1 = view.but_filter_breakfast
        val but2 = view.but_filter_secondbreakfast
        val but3 = view.but_filter_dinner
        val but4 = view.but_filter_dessert
        val but5 = view.but_filter_tea
        val but6 = view.but_filter_supper
        val but7 = view.but_filter_snacks
        val but8 = view.but_filter_training
        var curretlyViewedProductList = view.currently_viewed_list_of_product

        //create listOf buttons for eases editing them in future
        buttonFilterList = listOf<Button>(
            but1, but2, but3, but4, but5,
            but6, but7, but8,butAll
        )

        //add onClick on the buttons responsible for displaying the list of products that the user has previously selected for the meal that should now be consumed
        for (button in buttonFilterList) button.setOnClickListener(this)

        //set a background for the button showing the currently updated meal
        if (!autoSuggestMeal) {
            setBackgroundForButtonShowingCurrentMeal(buttonFilterList, 8)
        } else {
            setBackgroundForButtonShowingCurrentMeal(buttonFilterList, meal)
        }
        //show name of current meal
        view?.text_meal_name?.text = getString(R.string.current_meal).plus("\n${buttonFilterList[meal].text}")
        if (autoSuggestMeal) view?.currently_viewed_list_of_product?.text =
            "List of: ".plus("\n${buttonFilterList[meal].text.toString()}") else view?.currently_viewed_list_of_product?.text =
            "List of:\nAll"

        return view
    }

    private fun loadingProductListWithAutoSuggest() {
        if (!autoSuggestMeal) {
            loadProdactsFromDataBase(9, false)
        } else {
            loadProdactsFromDataBase(meal + 1, true)
        }
    }

    private fun setBackgroundForButtonShowingCurrentMeal(
        buttonFilterList: List<Button>,
        meal: Int
    ) {
        buttonFilterList[meal].setBackgroundResource(R.drawable.selected_frame_shape_but)
        mealScrollView.post { kotlin.run {
            mealScrollView.scrollTo(buttonFilterList[meal].x.toInt(),0)
        }
        }
    }

    private fun doubleColoredText(text1: String, text2: String): SpannableString{
        val coloredText = SpannableString("$text1$text2")
        coloredText.setSpan(ForegroundColorSpan(Color.RED),coloredText.length - text2.length,coloredText.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return coloredText
    }

    private fun readNotificationAlertTime(): Int{

        //read notification state and time
        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(MAIN_PREF,0)

        val notificationState = arrayOf<Boolean>(
            sharedPreferences.getBoolean(PREF_BREAKFAST_NOTIFICATION,false),
            sharedPreferences.getBoolean(PREF_SECOND_BREAKFAST_NOTIFICATION,false),
            sharedPreferences.getBoolean(PREF_DINNER_NOTIFICATION,false),
            sharedPreferences.getBoolean(PREF_DESSERT_NOTIFICATION,false),
            sharedPreferences.getBoolean(PREF_TEA_NOTIFICATION,false),
            sharedPreferences.getBoolean(PREF_SUPPER_NOTIFICATION,false),
            sharedPreferences.getBoolean(PREF_SNACKS_NOTIFICATION,false),
            sharedPreferences.getBoolean(PREF_TRAINING_NOTIFICATION,false)
        )

        val notificationTime = arrayOf<String?>(
            sharedPreferences.getString(PREF_BREAKFAST_NOTIFICATION_TIME,"00:00"),
            sharedPreferences.getString(PREF_SECOND_BREAKFAST_NOTIFICATION_TIME,"00:00"),
            sharedPreferences.getString(PREF_DINNER_NOTIFICATION_TIME,"00:00"),
            sharedPreferences.getString(PREF_DESSERT_NOTIFICATION_TIME,"00:00"),
            sharedPreferences.getString(PREF_TEA_NOTIFICATION_TIME,"00:00"),
            sharedPreferences.getString(PREF_SUPPER_NOTIFICATION_TIME,"00:00"),
            sharedPreferences.getString(PREF_SNACKS_NOTIFICATION_TIME,"00:00"),
            sharedPreferences.getString(PREF_TRAINING_NOTIFICATION_TIME,"00:00")
        )

        var meal = 0
        val timeNow = Calendar.getInstance()
        val formatTimeNow = SimpleDateFormat("HH:mm").format(timeNow.time) //get current time
        val localTme = LocalTime.of(formatTimeNow.substring(0,2).toInt(),formatTimeNow.substring(3,5).toInt()) //exchanging string format to LocalTime to making compares with alarms

        for (index in notificationTime.indices){

            //exchanging string(time) to LocalTime for compares current time witch two alarms
            val localTime1 = LocalTime.of(notificationTime[index]?.substring(0,2)!!.toInt(),notificationTime[index]?.substring(3,5)!!.toInt())
            val localTime2 = LocalTime.of(
                notificationTime[if ((index + 1) < notificationTime.size - 1) index + 1 else notificationTime.size - 1]?.substring(
                    0,
                    2
                )!!.toInt(),
                notificationTime[if ((index + 1) < notificationTime.size - 1) index + 1 else notificationTime.size - 1]?.substring(
                    3,
                    5
                )!!.toInt()
            )

            //compares the present time with the meal time and returns the number that corresponds to the meal you want to make now
            if(localTme.isAfter(localTime1) && localTme.isBefore(localTime2)){
                meal = index
            }else if (localTme.isAfter(localTime1) && index == notificationTime.size - 1){
                meal = index
            }else if (localTme == localTime1){
                meal = index
                break
            }
        }

        //check which alarm is on/off. If alarm is off, meal number is reduce, else loop is break and function is return current meal number(meal which should be consumed now)
        for (state in meal downTo 0){
            if (!notificationState[state] && meal != 0){
                meal-=1
            }else {
                break
            }
        }

        return meal
    }

    override fun onResume() {
        super.onResume()
        val view = this.view

        //read any product added to current day and calculate all nutrients
        val nutrientsSumArray = dbManager.readDayTable(date.toString())

        view?.day_sum_kcal?.text = nutrientsSumArray[0].toInt().toString()
        view?.day_sum_fat?.text = "%.1f".format(nutrientsSumArray[1]).replace(',','.')
        view?.day_sum_carbo?.text = "%.1f".format(nutrientsSumArray[2]).replace(',','.')
        view?.day_sum_pro?.text = "%.1f".format(nutrientsSumArray[3]).replace(',','.')

    }

    private fun loadProdactsFromDataBase(currentMeal: Int, autoSuggest: Boolean) {

        if(productList.size > 0) productList.clear()

        dbManager = MyDatabaseHelper(requireContext())
        val cursor = dbManager.readAllData()

        if (cursor.moveToNext()){

            var name: String
            var manufacturer: String
            var kcal: Int
            var fat: Double
            var protein: Double
            var carbo: Double
            var weight: Int
            var lastAddedWeight: Int
            var favorite: Int
            var id: Int

            do{

                name = cursor.getString(cursor.getColumnIndex("Name"))
                manufacturer = cursor.getString(cursor.getColumnIndex("Manufacturer"))
                kcal = cursor.getInt(cursor.getColumnIndex("Kcal"))
                fat = cursor.getDouble(cursor.getColumnIndex("Fat"))
                protein = cursor.getDouble(cursor.getColumnIndex("Protein"))
                carbo = cursor.getDouble(cursor.getColumnIndex("Carbohydrates"))
                weight = cursor.getInt(cursor.getColumnIndex("Weight"))
                lastAddedWeight = cursor.getInt(cursor.getColumnIndex("Portion"))
                favorite = cursor.getInt(cursor.getColumnIndex("Favorite"))
                id = cursor.getInt(cursor.getColumnIndex("ID_pro"))

                if (!autoSuggest && currentMeal == 9) {
                    if (this.requireContext().getSharedPreferences(MAIN_PREF, 0)
                            .getString(PREF_CURRENTLY_VIEWED_LIST, "All") == "All"
                    ) { //load list of all product from DB when autosuggestion is off
                        productList.add(
                            Product(name, manufacturer, kcal, protein, carbo, fat, weight,lastAddedWeight, favorite, id)
                        )
                    } else if (!autoSuggest && cursor.getInt(
                            cursor.getColumnIndex(
                                this.requireContext().getSharedPreferences(MAIN_PREF, 0)
                                    .getString(PREF_CURRENTLY_VIEWED_LIST, "All")
                            )
                        ) > 0
                    ) { //loading the product list of the last viewed meal when autosuggestion is off
                        productList.add(
                            Product(name, manufacturer, kcal, protein, carbo, fat, weight,lastAddedWeight, favorite, id)
                        )
                    }
                } else if (currentMeal in 1..8 ){
                    if (this.requireContext().getSharedPreferences(MAIN_PREF, 0)
                            .getString(PREF_CURRENTLY_VIEWED_LIST, "All") == "All"
                    ) { //load list of product from suggested meal when autosuggestion is on
                        if (cursor.getInt(cursor.getColumnIndex(findMealByNum(currentMeal))) > 0) {
                            productList.add(
                                Product(name, manufacturer, kcal, protein, carbo, fat, weight,lastAddedWeight, favorite, id)
                            )

                        }
                    } else if (cursor.getInt(
                            cursor.getColumnIndex(
                                this.requireContext().getSharedPreferences(MAIN_PREF, 0)
                                    .getString(PREF_CURRENTLY_VIEWED_LIST, "All")
                            )
                        ) > 0
                    ) { //load list of product from meal PREF_CURRENTLY_VIEWED_LIST when autosuggestion is on
                        productList.add(
                            Product(name, manufacturer, kcal, protein, carbo, fat, weight,lastAddedWeight, favorite, id)
                        )
                    }
                }
            } while (cursor.moveToNext())

            var groupSort = this.productList.groupBy { it.favorite }
            var map1 = groupSort[1]?.sortedBy { it.name }
            var map2 = groupSort[0]?.sortedBy { it.name }
            productList.clear()
            if (map1 != null) {
                productList.addAll(map1)
            }
            if (map2 != null) {
                productList.addAll(map2)
            }
        }
    }

    private fun readData(
        kcalUserPref: TextView,
        fatUserPref: TextView,
        proUserPref: TextView,
        carboUserPref: TextView
    ) {

        val shader = requireContext().getSharedPreferences(MAIN_PREF,0)
        kcalUserPref.text = shader?.getInt(PREF_KCAL,0).toString()
        fatUserPref.text = shader?.getInt(PREF_FAT,0).toString()
        carboUserPref.text = shader?.getInt(PREF_CARBO,0).toString()
        proUserPref.text = shader?.getInt(PREF_PRO,0).toString()
//        autoSuggestMeal = shader!!.getBoolean(PREF_AUTO_SUGGEST_MEAL,true)

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        adapterProduct.filter.filter(newText)

        return true
    }

    private fun refreshFragment(){

        adapterProduct!!.notifyDataSetChanged()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            if (data?.getBooleanExtra("newProductCreated",false) == true) {
                Handler().postDelayed({
                    productList.clear()
                    loadProdactsFromDataBase(meal+1, autoSuggestMeal)
                    refreshFragment()
                },100)
            }
            Handler().postDelayed({
                //read any product added to current day and calculate all nutrients
                val nutrientsSumArray = dbManager.readDayTable(date.toString())

                view?.home_user_pref_kcal?.text = nutrientsSumArray[0].toInt().toString()
                view?.home_user_pref_pro?.text = "%.1f".format(nutrientsSumArray[1]).replace(',','.')
                view?.home_user_pref_fat?.text = "%.1f".format(nutrientsSumArray[2]).replace(',','.')
                view?.home_user_pref_carbo?.text = "%.1f".format(nutrientsSumArray[3]).replace(',','.')
            },2000)
        }

    }

    override fun onClick(v: View?) {
        v as Button

        for (but in buttonFilterList){
            if (v == but) {
                v.setBackgroundResource(R.drawable.selected_frame_shape_but)
//                view?.text_meal_name?.text = doubleColoredText(getString(R.string.current_meal), buttonFilterList[meal].text.toString())
                view?.text_meal_name?.text = getString(R.string.current_meal).plus("\n${buttonFilterList[meal].text}")
//                view?.currently_viewed_list_of_product?.text = doubleColoredText("List of: ", v.text.toString())
                view?.currently_viewed_list_of_product?.text = "List of: ".plus("\n${v.text}")

            } else {
                but.setBackgroundResource(R.drawable.light_gray_bt_shape)
            }
        }

        view?.context?.getSharedPreferences(MAIN_PREF, 0)?.edit()
            ?.putString(PREF_CURRENTLY_VIEWED_LIST, findMealByNum(findMealByTitle(v)))?.apply()

        autoSuggestMeal = false //Change state because i need load list with all product after add from this list (all product list) product to the day when autosuggestion is on - this operation don't change settings

        loadProdactsFromDataBase(findMealByTitle(v),false)


        refreshFragment()
    }

    private fun findMealByTitle(v: View?): Int{

        v as Button

        return when(v){
            but_filter_breakfast -> 1
            but_filter_secondbreakfast -> 2
            but_filter_dinner -> 3
            but_filter_dessert -> 4
            but_filter_tea -> 5
            but_filter_supper -> 6
            but_filter_snacks -> 7
            but_filter_training -> 8
            else -> 9
        }
    }

    private fun findMealByNum(num: Int): String{

        return when(num){
            1 -> "Breakfast"
            2 -> "SecondBreakfast"
            3 -> "Dinner"
            4 -> "Dessert"
            5 -> "Tea"
            6 -> "Supper"
            7 -> "Snacks"
            8 -> "Training"
            else -> "All"
        }
    }
}
