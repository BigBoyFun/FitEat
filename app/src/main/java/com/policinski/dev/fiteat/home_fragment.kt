package com.policinski.dev.fiteat

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.arrayMapOf
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home_fragment.view.*
import kotlinx.android.synthetic.main.fragment_home_fragment.view.day_sum_kcal
import kotlinx.android.synthetic.main.fragment_home_fragment.view.selected_date_tx
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class home_fragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private val MAIN_PREF = "MAIN_PREF"
    private val PREF_CARBO = "PREF_CARBO"
    private val PREF_FAT = "PREF_FAT"
    private val PREF_KCAL = "PREF_KCAL"
    private val PREF_PRO = "PREF_PRO"
    private val PREF_MEAL_BREAKFAST = "PREF_BREAKFAST"
    private val PREF_MEAL_SECOND_BREAKFAST = "PREF_MEAL_SECOND_BREAKFAST"
    private val PREF_MEAL_DINNER = "PREF_MEAL_DINNER"
    private val PREF_MEAL_DESSERT = "PREF_MEAL_DESSERT"
    private val PREF_MEAL_TEA = "PREF_MEAL_TEA"
    private val PREF_MEAL_SUPPER = "PREF_MEAL_SUPPER"
    private val PREF_MEAL_SNACKS = "PREF_MEAL_SNACKS"
    private val PREF_MEAL_TRAINING = "PREF_MEAL_TRAINING"
    private val header: MutableList<String> = ArrayList()
    private lateinit var mealAdapter: MealRecycleListAdapter
    private lateinit var mealList: MutableList<Meal>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_home_fragment, container, false)

//        val db = MyDatabaseHelper(requireContext())////////////////////////////////////////////////////////////////////////////////////////// FOR DELETE!!!!!!!!!!!!!!
//        v.textView19.setOnClickListener{db.addRowWeight()} ////////////////////////////////////////////////////////////////////////////////// FOR DELETE!!!!!!!!!!!!!!!!

        val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val nextDay = v.next_date_bt
        val backDay = v.bcak_date_bt
        var selectedDayView = v.selected_date_tx

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)

        val monthsArray = arrayOf(getString(R.string.month_01),getString(R.string.month_02),getString(
                    R.string.month_03),getString(R.string.month_04),getString(R.string.month_05),getString(
                                R.string.month_06),getString(R.string.month_07),getString(R.string.month_08),getString(
                                            R.string.month_09),getString(R.string.month_10),getString(
                                                        R.string.month_11),getString(R.string.month_12))

        var plusDay: Long = 0 //variable for detect next day
        var minusDay: Long = 0 //variable for detect previous day
        var newDate: LocalDate

        selectedDayView.text = "${date.dayOfMonth} ${monthsArray[date.monthValue - 1]} ${date.year}"

        selectedDayView.setOnClickListener{

            val datePickerDialog: DatePickerDialog = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{view, year, mothOfYear, dayOfMonth ->

                selectedDayView.text = "$dayOfMonth ${monthsArray[mothOfYear]} $year "
                plusDay = 0.toLong()
                minusDay = 0.toLong()
                val month = "%02d".format(mothOfYear + 1)
                val day ="%02d".format(dayOfMonth)
                showDayPropertis(v,"$year-$month-$day")
                showMeals(v,"$year-$month-$day")


            },year,month,day)

            datePickerDialog.show()

        }

        showDayPropertis(v,date.toString())

        //select date by button
        //one day back
        nextDay.setOnClickListener {
            if (minusDay > 0) {
                minusDay -= 1
                newDate = date.minusDays(minusDay)
                selectedDayView.text = "${newDate.dayOfMonth} ${monthsArray[newDate.monthValue - 1]} ${newDate.year}"
                showDayPropertis(v,newDate.toString())
                showMeals(v,newDate.toString())

            } else if (minusDay == 0.toLong()){
                plusDay += 1
                newDate = date.plusDays(plusDay)
                selectedDayView.text = "${newDate.dayOfMonth} ${monthsArray[newDate.monthValue - 1]} ${newDate.year}"
                showDayPropertis(v,newDate.toString())
                showMeals(v,newDate.toString())

            }
        }

        //one day forward
        backDay.setOnClickListener {
            if (plusDay > 0){
                plusDay -= 1
                newDate = date.plusDays(plusDay)
                selectedDayView.text = "${newDate.dayOfMonth} ${monthsArray[newDate.monthValue - 1]} ${newDate.year}"
                showDayPropertis(v,newDate.toString())
                showMeals(v,newDate.toString())
            } else if (plusDay == 0.toLong()){
                minusDay += 1
                newDate = date.minusDays(minusDay)
                selectedDayView.text = "${newDate.dayOfMonth} ${monthsArray[newDate.monthValue - 1]} ${newDate.year}"
                showDayPropertis(v,newDate.toString())
                showMeals(v,newDate.toString())
            }
        }

        showMeals(v,date.toString())

        return v
    }

    private fun showDayPropertis(v: View?, date: String) {

        //init DB, SharedPref, read nutrients from DB to array
        val db = MyDatabaseHelper(requireContext())
        val nutrientsSumArray = db.readDayTable(date)
        val sharedPreferences = requireContext().getSharedPreferences(MAIN_PREF,0)

        //find view in homeFragment
        val daySumKcal = v?.day_sum_kcal
        val daySumPro = v?.day_sum_pro
        val daySumFat = v?.day_sum_fat
        val daySumCarbo = v?.day_sum_carbo
        val circularProgressBar = v?.progress_barr
        val progressInPercent = v?.progress_in_percent
        val homeUserPrefKcal = v?.home_user_pref_kcal
        val homeUserPrefFat = v?.home_user_pref_fat
        val homeUserPrefCarbo = v?.home_user_pref_carbo
        val homeUserPrefPro = v?.home_user_pref_pro

        //read pref from sharedPreferences
        var prefkcal = 0
        var prefPro = 0.0
        var prefFat = 0.0
        var prefCarbo = 0.0

        //read nutrients preferences at current day from database
        val cursor = db.readDailyGoalNutrients(date)

        if (cursor.moveToFirst() && cursor.getInt(cursor.getColumnIndex("Kcal")) != 0){
            prefkcal = cursor.getInt(cursor.getColumnIndex("Kcal"))
            prefPro = cursor.getDouble(cursor.getColumnIndex("Protein"))
            prefFat = cursor.getDouble(cursor.getColumnIndex("Fat"))
            prefCarbo = cursor.getDouble(cursor.getColumnIndex("Carbohydrates"))

        } else {
            //read nutrients preferences at current day from sharedPreferences and save them to database
            prefkcal = sharedPreferences.getInt(PREF_KCAL,0)
            prefPro = sharedPreferences.getInt(PREF_PRO,0).toDouble()
            prefFat = sharedPreferences.getInt(PREF_FAT,0).toDouble()
            prefCarbo = sharedPreferences.getInt(PREF_CARBO,0).toDouble()

        }

        //set value for view in homeFragment(from db and from sharedPref)
        daySumKcal?.text = "${nutrientsSumArray[0].toInt()}"
        homeUserPrefKcal?.text = "$prefkcal"
        daySumPro?.text = "%.1f".format(nutrientsSumArray[1]).replace(',','.')
        homeUserPrefPro?.text = "${prefPro.toInt()}"
        daySumFat?.text = "%.1f".format(nutrientsSumArray[2]).replace(',','.')
        homeUserPrefFat?.text = "${prefFat.toInt()}"
        daySumCarbo?.text = "%.1f".format(nutrientsSumArray[3]).replace(',','.')
        homeUserPrefCarbo?.text = "${prefCarbo.toInt()}"

        circularProgressBar?.max = prefkcal
        circularProgressBar?.progress = nutrientsSumArray[0].toInt()
        val kcalProgress= (nutrientsSumArray[0] / (prefkcal / 100)).toInt()
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             if (kcalProgress > 100) {
                 progressInPercent?.setTextColor(resources.getColor(R.color.custom_red,null))
             } else {
                 progressInPercent?.setTextColor(resources.getColor(R.color.colorAccent,null))
             }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            progressInPercent?.setTextColor(resources.getColor(R.color.custom_red,null))
        }
        progressInPercent?.text = "$kcalProgress%"

    }

    private fun showMeals(v: View,date: String){ //// DO POPRAWKI DO POPRAWKI DO POPRAWKI DO POPRAWKI DO POPRAWKI DO POPRAWKI DO POPRAWKI DO POPRAWKI DO POPRAWKI DO POPRAWKI DO POPRAWKI DO POPRAWKI DO POPRAWKI DO POPRAWKI DO POPRAWKI

        var calculatedMealNutrients: MutableList<Product> = mutableListOf()
        var allDayMealListProduct: MutableList<MutableList<Product>> = mutableListOf()
        val selectedMeals = readSelectedMealsByUser()

        val db = MyDatabaseHelper(requireContext())

        for (i in selectedMeals.keys) {
            var product: Product = Product()
            val cursor = db.readMealFromDay(date, i)

            allDayMealListProduct.add(cursor)

            for (item in cursor){
                product.kcal += item.kcal
                product.carbo += item.carbo
                product.fat += item.fat
                product.protein += item.protein
                product.weight += item.weight
            }

            calculatedMealNutrients.add(product)

        }

        v.recycle_view_meal.apply {
            layoutManager = LinearLayoutManager(requireContext())
            mealAdapter = MealRecycleListAdapter()
            adapter = mealAdapter
        }

        mealAdapter.submitList(calculatedMealNutrients, allDayMealListProduct, selectedMeals)

    }

    private fun readSelectedMealsByUser(): Map<Int, String> {

        val mealSelectedArray = arrayMapOf<Int, String>()

        val sharedPreferences = requireContext().getSharedPreferences(MAIN_PREF,0)
        val breakfast = sharedPreferences.getBoolean(PREF_MEAL_BREAKFAST,true)
        val secondBreakfast = sharedPreferences.getBoolean(PREF_MEAL_SECOND_BREAKFAST,true)
        val dinner = sharedPreferences.getBoolean(PREF_MEAL_DINNER,true)
        val dessert = sharedPreferences.getBoolean(PREF_MEAL_DESSERT,true)
        val tea = sharedPreferences.getBoolean(PREF_MEAL_TEA,true)
        val supper = sharedPreferences.getBoolean(PREF_MEAL_SUPPER,true)
        val snacks = sharedPreferences.getBoolean(PREF_MEAL_SNACKS,true)
        val training = sharedPreferences.getBoolean(PREF_MEAL_TRAINING,true)

        if (breakfast) mealSelectedArray[1] = "Breakfast"
        if (secondBreakfast) mealSelectedArray[2] = "Second breakfast"
        if (dinner) mealSelectedArray[3] = "Dinner"
        if (dessert) mealSelectedArray[4] = "Dessert"
        if (tea) mealSelectedArray[5] = "Tea"
        if (supper) mealSelectedArray[6] = "Supper"
        if (snacks) mealSelectedArray[7] = "Snacks"
        if (training) mealSelectedArray[8] = "Training"

        return mealSelectedArray
    }

    companion object {
    }
}
