package com.policinski.dev.fiteat

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.arrayMapOf
import kotlinx.android.synthetic.main.activity_add_product_to_day.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

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

        //enables buttons that are responsible for selecting available meals (selected by the user)
        for (i in mealArray.keys) array[i-1].isEnabled = true

        //searching meal with should be upgraded by selected product
        meal = readNotificationAlertTime()+1
        //highlights the button that is responsible for selecting a meal that should be enriched with the selected product
        array[if (meal > 0) meal-1 else 0].setBackgroundResource(R.drawable.green_bt_shape)

        //show selected meal name
        when(readNotificationAlertTime()+1){
            1 -> mealNameTv.text = getString(R.string.breakfast_1)
            2 -> mealNameTv.text = getString(R.string.second_breakfast_2)
            3 -> mealNameTv.text = getString(R.string.dinner_3)
            4 -> mealNameTv.text = getString(R.string.dessert_4)
            5 -> mealNameTv.text = getString(R.string.tea_5)
            6 -> mealNameTv.text = getString(R.string.supper_6)
            7 -> mealNameTv.text = getString(R.string.snacks_7)
            8 -> mealNameTv.text = getString(R.string.training_8)
        }


        //changing highlights on button which response for selected meal and shows meal name in textView
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

            //shows meal name in textView
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

    private fun readNotificationAlertTime(): Int{

        //read notification state and time
        val sharedPreferences = getSharedPreferences(MAIN_PREF,0)

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

}