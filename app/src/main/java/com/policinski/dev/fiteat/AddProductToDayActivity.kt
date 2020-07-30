package com.policinski.dev.fiteat

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.SeekBar
import androidx.collection.arrayMapOf
import kotlinx.android.synthetic.main.activity_add_product_to_day.*
import java.time.LocalDate

lateinit var array: ArrayList<Button>
private val MAIN_PREF = "MAIN_PREF"
private val PREF_MEAL_BREAKFAST = "PREF_BREAKFAST"
private val PREF_MEAL_SECOND_BREAKFAST = "PREF_MEAL_SECOND_BREAKFAST"
private val PREF_MEAL_DINNER = "PREF_MEAL_DINNER"
private val PREF_MEAL_DESSERT = "PREF_MEAL_DESSERT"
private val PREF_MEAL_TEA = "PREF_MEAL_TEA"
private val PREF_MEAL_SUPPER = "PREF_MEAL_SUPPER"
private val PREF_MEAL_SNACKS = "PREF_MEAL_SNACKS"
private val PREF_MEAL_TRAINING = "PREF_MEAL_TRAINING"
var meal = 1

class AddProductToDayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_to_day)


        var selectedProductName: String = intent.getStringExtra("PRODUCT_NAME")

        val db = MyDatabaseHelper(this)
        val product = db.getSelectedProduct(selectedProductName)!!

        //init views
        val productName = product_name
        var input_weight = input_weight
        var calculateKcal = calculate_kcal
        var calculatePro = calculate_pro
        var calculateFat = calculate_fat
        var calculateCarbo = calculate_carbo
        var read_weight_tv = read_weight_tv
        val seekBar = seekBar
        var mealNameTv = meal_name_tv
        val onBt = ok_bt
        val cancelBt = cancel_bt

        //initiation of buttons responsible for choosing the right meal to which we want to add the selected product
        array = arrayListOf<Button>(button_1,
            button_2,
            button_3,
            button_4,
            button_5,
            button_6,
            button_7,
            button_8)

        val mealArray = readSelectedMealsByUser()

        //FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK FOR REWORK
        //enables buttons that are responsible for selecting available meals (selected by the user)
        for (i in mealArray.keys) array[i-1].isEnabled = true

        for ((index, item) in array.withIndex()) item.setOnClickListener { v ->
            v as Button
            meal = v.text.toString().toInt()

            for (item in array){
                if (item.text.toString().toInt() != meal){
                    item.setBackgroundResource(R.drawable.gray_bt_shape)
                }else{
                    item.setBackgroundResource(R.drawable.green_bt_shape)
                }
            }

            when(array.indexOf(v)){
                0 -> mealNameTv.text = getString(R.string.breakfast_1)
                1 -> mealNameTv.text = getString(R.string.second_breakfast_2)
                2 -> mealNameTv.text = getString(R.string.dinner_3)
                3 -> mealNameTv.text = getString(R.string.dessert_4)
                4 -> mealNameTv.text = getString(R.string.tea_5)
                5 -> mealNameTv.text = getString(R.string.supper_6)
                6 -> mealNameTv.text = getString(R.string.snacks_7)
                7 -> mealNameTv.text = getString(R.string.training_8)
            }

        }

        //set text on all textViews and progress on seekBar, by current product specification
        productName.text = product.name
        calculateKcal.text = product.kcal.toString()
        calculatePro.text = product.protein.toString()
        calculateFat.text = product.fat.toString()
        calculateCarbo.text = product.carbo.toString()
        input_weight.setText("${product.weight}")
        read_weight_tv.text = "${product.weight}"
        seekBar.progress = product.weight

        // setting progress on seekBar by writing in editText
        input_weight.addTextChangedListener(object : TextWatcher {
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
                if (s.toString().isEmpty()){
                    seekBar.progress = 0
                    read_weight_tv.text = "0 g"
                }else {
                    seekBar.progress = s.toString().toInt()
                    if (s.toString().toLong() > 999){
                        read_weight_tv.text = "999 g"
                    }else {
                        read_weight_tv.text = "$s g"
                    }
                }

            }

        })

        //calculate nutrients by change seekBar state
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                read_weight_tv.setText("$progress g")
                calculateKcal.text = ((product.kcal * progress) / product.weight).toString()
                calculatePro.text = "%.2f".format((product.protein * progress) / product.weight).replace(',','.')
                calculateFat.text = "%.2f".format((product.fat * progress) / product.weight).replace(',','.')
                calculateCarbo.text = "%.2f".format((product.carbo * progress) / product.weight).replace(',','.')
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().toString()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        //add meal to current day and close dialog
        ok_bt.setOnClickListener{
            db.addProductToDay(data,
                product.name,
                calculateKcal.text.toString().toInt(),
                calculatePro.text.toString().replace(',','.').toDouble(),
                calculateCarbo.text.toString().replace(',','.').toDouble(),
                calculateFat.text.toString().replace(',','.').toDouble(),
                meal,seekBar.progress)

            setResult(Activity.RESULT_OK, intent)
            finish()

        }

        //close dialog with no adding meal to current day
        cancel_bt.setOnClickListener{
            this.finish()
        }

    }

    private fun readSelectedMealsByUser(): Map<Int, String> {

        val mealSelectedArray = arrayMapOf<Int, String>()

        val sharedPreferences = getSharedPreferences(MAIN_PREF,0)
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

}