package com.policinski.dev.fiteat

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.arrayMapOf
import kotlinx.android.synthetic.main.activity_add_product_to_day.*
import kotlinx.android.synthetic.main.fragment_settings_fragment.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

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
var editProductWeight: Int = 1
lateinit var currentProductWeight: TextView
lateinit var productName: TextView
lateinit var calculateKcal: TextView
lateinit var calculatePro: TextView
lateinit var calculateFat: TextView
lateinit var calculateCarbo: TextView
lateinit var product: Product
lateinit var array: ArrayList<ToggleButton>
lateinit var inputWeight: EditText
lateinit var onBt: Button
lateinit var cancelBt: Button
lateinit var btOneLess: Button
lateinit var btOneMore: Button
lateinit var btSetWeight: ToggleButton
lateinit var btSetPortion: ToggleButton
lateinit var tvMultiplierWeight: TextView
lateinit var btLastWeightPortion: Button

class AddProductToDayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_to_day)


        var selectedProductName: String = intent.getStringExtra("PRODUCT_NAME")

        val db = MyDatabaseHelper(this)
        product = db.getSelectedProduct(selectedProductName)!!

        //init views
        productName = product_name
        currentProductWeight = tv_current_product_weigth
        calculateKcal = calculate_kcal
        calculatePro = calculate_pro
        calculateFat = calculate_fat
        calculateCarbo = calculate_carbo
        inputWeight = input_weight
        onBt = ok_bt
        cancelBt = cancel_bt
        btOneLess = bt_one_less
        btOneMore = bt_one_more
        btSetWeight = button_set_weight
        btSetPortion = button_set_portion
        tvMultiplierWeight = tv_multiplier_weight
        btLastWeightPortion = bt_last_weight_portion

        //set product name to top text view and show weight
        productName.text = product.name
        currentProductWeight.text = product.weight.toString()

        //set state for portion and weight bt based on product weight
        //if btSetWeight isChecked = true editProductWeight = Product.Weight else set 1 like a multiplier
        if (product.weight != 100) {
            btSetPortion.isChecked = true
            btSetWeight.isChecked = false
            inputWeight.setText("1") //set inputWeight on editProductWeight like a multiplier
            tvMultiplierWeight.text = "x"
        } else {
            btSetPortion.isChecked = false
            btSetWeight.isChecked = true
            editProductWeight = product.weight
            inputWeight.setText(product.weight.toString()) //set inputWeight on product weight
            tvMultiplierWeight.text = ".g"
        }

        //set listener for btSetWeight and brSetPortion
        btSetWeight.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                btSetPortion.isChecked = false
                editProductWeight = product.weight
                inputWeight.setText(editProductWeight.toString())
                tvMultiplierWeight.text = ".g"
                calculateNut(product.weight)
                closeSoftKeyboard(this, inputWeight)
            } else {
                btSetPortion.isChecked = true
            }
        }
        btSetPortion.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                btSetWeight.isChecked = false
                editProductWeight = 1
                inputWeight.setText(editProductWeight.toString())
                tvMultiplierWeight.text = "x"
                calculateNut(product.weight)
                closeSoftKeyboard(this, inputWeight)
            } else {
                btSetWeight.isChecked = true
            }
        }

        btOneLess.setOnClickListener {

            if (editProductWeight != 0) {
                editProductWeight -= 1
                inputWeight.setText(editProductWeight.toString())
            }else {
                Toast.makeText(this, getString(R.string.no_less_them_zero), Toast.LENGTH_SHORT)
                    .show()
            }

            closeSoftKeyboard(this, inputWeight)

        }
        btOneMore.setOnClickListener {

            editProductWeight += 1
            inputWeight.setText(editProductWeight.toString())
            closeSoftKeyboard(this, inputWeight)

        }

        // set text for input weight on last added weight or portion
        btLastWeightPortion.setOnClickListener {

            if (product.lastAddedWeight != 0){
                if (product.lastAddedWeight % product.weight == 0) {
                    btSetPortion.isChecked = true
                    editProductWeight = product.lastAddedWeight / product.weight
                    inputWeight.setText(editProductWeight.toString())
                } else {
                    btSetWeight.isChecked = true
                    editProductWeight = product.lastAddedWeight
                    inputWeight.setText(editProductWeight.toString())
                }
            } else {
                Toast.makeText(this,getString(R.string.newr_added_no_last_weight), Toast.LENGTH_SHORT).show()
            }

            closeSoftKeyboard(this, inputWeight)

        }

        //initiation of buttons responsible for choosing the right meal to which we want to add the selected product
        array = arrayListOf<ToggleButton>(
            breakfast_but_suggestion_1,
            second_breakfast_but_suggestion_2,
            dinner_but_suggestion_3,
            dessert_but_suggestion_4,
            tea_but_suggestion_5,
            supper_but_suggestion_6,
            snacks_but_suggestion_7,
            training_but_suggestion_8
        )

        val mealArray = readSelectedMealsByUser()

        //enables buttons that are responsible for selecting available meals (selected by the user)
        for (i in mealArray.keys) array[i - 1].isEnabled = true

        //searching meal with should be upgraded by selected product
        meal = readNotificationAlertTime() + 1
        //highlights the button that is responsible for selecting a meal that should be enriched with the selected product
        array[if (meal > 0) meal - 1 else 0].isChecked = true


        //changing highlights on button which response for selected meal and shows meal name in textView
        for ((index, item) in array.withIndex()) item.setOnClickListener { v ->
            v as Button
//            meal = v.text.toString().toInt()
            meal = array.indexOf(v) + 1

            for (item in array) {
                if (array.indexOf(item) + 1 != meal && item.isEnabled) {
                    item.isChecked = false
                } else if (array.indexOf(item) + 1 == meal && item.isEnabled) {
                    item.isChecked = true
                }
            }

        }

        //set text on all textViews and progress on seekBar, by current product specification
//        if (product.lastAddedWeight != 0) {
//            calculateNut(product.lastAddedWeight)
//        } else {
//        }
        calculateNut(product.weight)

        // setting progress on seekBar by writing in editText
        inputWeight.addTextChangedListener(object : TextWatcher {
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

                try {
                    if (s.toString().toLong() > 999) {
                        inputWeight.setText("999")
                    } else {
                        if (btSetPortion.isChecked && !btSetWeight.isChecked) {
                            currentProductWeight.text = (product.weight * s.toString().toInt()).toString()
                            calculateNut(product.weight * s.toString().toInt())
                        } else if (btSetWeight.isChecked && !btSetPortion.isChecked) {
                            currentProductWeight.text = s.toString()
                            calculateNut(s.toString().toInt())
                        }
                    }
                } catch (e: NumberFormatException){
                }

            }

        })

        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().toString()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        //add meal to current day and close dialog
        ok_bt.setOnClickListener {
            if (inputWeight.text.toString().toInt() != 0) {
                db.addProductToDay(
                    data,
                    product.name,
                    calculateKcal.text.toString().toInt(),
                    calculatePro.text.toString().replace(',', '.').toDouble(),
                    calculateCarbo.text.toString().replace(',', '.').toDouble(),
                    calculateFat.text.toString().replace(',', '.').toDouble(),
                    meal,
                    if (btSetPortion.isChecked) product.weight * inputWeight.text.toString()
                        .toInt() else inputWeight.text.toString().toInt(),
                    product.id
                )

                setResult(Activity.RESULT_OK, intent)
                finish()

            } else {
                Toast.makeText(
                    this, getString(R.string.set_weight_portion_mor_them_zero),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //close dialog with no adding meal to current day
        cancel_bt.setOnClickListener {
            this.finish()
        }

    }

    private fun closeSoftKeyboard(context: Context, v: View) {
        val iMm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        iMm.hideSoftInputFromWindow(v.windowToken, 0)
        v.clearFocus()
    }

    private fun readSelectedMealsByUser(): Map<Int, String> {

        val mealSelectedArray = arrayMapOf<Int, String>()

        val sharedPreferences = getSharedPreferences(MAIN_PREF, 0)

        val breakfast = sharedPreferences.getBoolean(PREF_MEAL_BREAKFAST, true)
        val secondBreakfast = sharedPreferences.getBoolean(PREF_MEAL_SECOND_BREAKFAST, true)
        val dinner = sharedPreferences.getBoolean(PREF_MEAL_DINNER, true)
        val dessert = sharedPreferences.getBoolean(PREF_MEAL_DESSERT, true)
        val tea = sharedPreferences.getBoolean(PREF_MEAL_TEA, true)
        val supper = sharedPreferences.getBoolean(PREF_MEAL_SUPPER, true)
        val snacks = sharedPreferences.getBoolean(PREF_MEAL_SNACKS, true)
        val training = sharedPreferences.getBoolean(PREF_MEAL_TRAINING, true)

        if (breakfast) mealSelectedArray[1] =
            getString(R.string.breakfast_1) else breakfast_but_suggestion_1.isEnabled = false
        if (secondBreakfast) mealSelectedArray[2] =
            getString(R.string.second_breakfast_2) else second_breakfast_but_suggestion_2.isEnabled =
            false
        if (dinner) mealSelectedArray[3] =
            getString(R.string.dinner_3) else dinner_but_suggestion_3.isEnabled = false
        if (dessert) mealSelectedArray[4] =
            getString(R.string.dessert_4) else dessert_but_suggestion_4.isEnabled = false
        if (tea) mealSelectedArray[5] =
            getString(R.string.tea_5) else tea_but_suggestion_5.isEnabled = false
        if (supper) mealSelectedArray[6] =
            getString(R.string.supper_6) else supper_but_suggestion_6.isEnabled = false
        if (snacks) mealSelectedArray[7] =
            getString(R.string.snacks_7) else snacks_but_suggestion_7.isEnabled = false
        if (training) mealSelectedArray[8] =
            getString(R.string.training_8) else training_but_suggestion_8.isEnabled = false

        return mealSelectedArray
    }

    private fun readNotificationAlertTime(): Int {

        //read notification state and time
        val sharedPreferences = getSharedPreferences(MAIN_PREF, 0)

        val notificationState = arrayOf<Boolean>(
            sharedPreferences.getBoolean(PREF_BREAKFAST_NOTIFICATION, false),
            sharedPreferences.getBoolean(PREF_SECOND_BREAKFAST_NOTIFICATION, false),
            sharedPreferences.getBoolean(PREF_DINNER_NOTIFICATION, false),
            sharedPreferences.getBoolean(PREF_DESSERT_NOTIFICATION, false),
            sharedPreferences.getBoolean(PREF_TEA_NOTIFICATION, false),
            sharedPreferences.getBoolean(PREF_SUPPER_NOTIFICATION, false),
            sharedPreferences.getBoolean(PREF_SNACKS_NOTIFICATION, false),
            sharedPreferences.getBoolean(PREF_TRAINING_NOTIFICATION, false)
        )

        val notificationTime = arrayOf<String?>(
            sharedPreferences.getString(PREF_BREAKFAST_NOTIFICATION_TIME, "00:00"),
            sharedPreferences.getString(PREF_SECOND_BREAKFAST_NOTIFICATION_TIME, "00:00"),
            sharedPreferences.getString(PREF_DINNER_NOTIFICATION_TIME, "00:00"),
            sharedPreferences.getString(PREF_DESSERT_NOTIFICATION_TIME, "00:00"),
            sharedPreferences.getString(PREF_TEA_NOTIFICATION_TIME, "00:00"),
            sharedPreferences.getString(PREF_SUPPER_NOTIFICATION_TIME, "00:00"),
            sharedPreferences.getString(PREF_SNACKS_NOTIFICATION_TIME, "00:00"),
            sharedPreferences.getString(PREF_TRAINING_NOTIFICATION_TIME, "00:00")
        )

        var meal = 0
        val timeNow = Calendar.getInstance()
        val formatTimeNow = SimpleDateFormat("HH:mm").format(timeNow.time) //get current time
        val localTme = LocalTime.of(
            formatTimeNow.substring(0, 2).toInt(),
            formatTimeNow.substring(3, 5).toInt()
        ) //exchanging string format to LocalTime to making compares with alarms

        for (index in notificationTime.indices) {

            //exchanging string(time) to LocalTime for compares current time witch two alarms
            val localTime1 = LocalTime.of(
                notificationTime[index]?.substring(0, 2)!!.toInt(),
                notificationTime[index]?.substring(3, 5)!!.toInt()
            )
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
            if (localTme.isAfter(localTime1) && localTme.isBefore(localTime2)) {
                meal = index
            } else if (localTme.isAfter(localTime1) && index == notificationTime.size - 1) {
                meal = index
            } else if (localTme == localTime1) {
                meal = index
                break
            }
        }

        //check which alarm is on/off. If alarm is off, meal number is reduce, else loop is break and function is return current meal number(meal which should be consumed now)
        for (state in meal downTo 0) {
            if (!notificationState[state] && meal != 0) {
                meal -= 1
            } else {
                break
            }
        }

        return meal
    }

    //calculate nutrients after change weight
    fun calculateNut(progress: Int) {
        calculateKcal.text = ((product.kcal * progress) / product.weight).toString()
        calculatePro.text =
            "%.2f".format((product.protein * progress) / product.weight).replace(',', '.')
        calculateFat.text =
            "%.2f".format((product.fat * progress) / product.weight).replace(',', '.')
        calculateCarbo.text =
            "%.2f".format((product.carbo * progress) / product.weight).replace(',', '.')
    }

    override fun onPause() {
        super.onPause()



    }
}