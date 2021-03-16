package com.policinski.dev.fiteat

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.fragment_settings_fragment.view.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class settings_fragment : Fragment(), View.OnClickListener {

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

    private val PREF_AUTO_SUGGEST_MEAL = "PREF_AUTO_SUGEST_MEAL"
    private val PREF_CURRENTLY_VIEWED_LIST = "PREF_CURRENTLY_VIEWED_LIST" //created for deleting product from specific meal(not from DB)

    lateinit var saveBt: Button //lateinit for make access to onClick function from onPause to make auto save by exit from settings activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_settings_fragment, container, false)

        view.context.getSharedPreferences(MAIN_PREF,0).edit().putString(PREF_CURRENTLY_VIEWED_LIST,"All").apply()

        val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        //init views from settings fragment
        val kcal = view.findViewById<EditText>(R.id.kcal_product_settings_dialog)
        val carbo = view.findViewById<EditText>(R.id.carbo_user_product_settings_dialog)
        val fat = view.findViewById<EditText>(R.id.fat_user_product_settings_dialog)
        val pro = view.findViewById<EditText>(R.id.pro_user_product_settings_dialog)
        val breakfast = view.findViewById<CheckBox>(R.id.breakfast_check_box)
        val secondBreakfast = view.findViewById<CheckBox>(R.id.second_breakfast_check_box)
        val dinner = view.findViewById<CheckBox>(R.id.dinner_check_box)
        val dessert = view.findViewById<CheckBox>(R.id.dessert_check_box)
        val tea = view.findViewById<CheckBox>(R.id.tea_check_box)
        val supper = view.findViewById<CheckBox>(R.id.supper_check_box)
        val snacks = view.findViewById<CheckBox>(R.id.snacks_check_box)
        val training = view.findViewById<CheckBox>(R.id.training_check_box)
        val clearUserHoises = view.findViewById<Button>(R.id.bt_clear_user_hoises)

        //init notification views
        val notificationBreakfastSwitch = view.notification_breakfast_switch
        val notificationSecondBreakfastSwitch = view.second_breakfast_notification_switch
        val notificationDinnerSwitch = view.dinner_notification_switch
        val notificationDessertSwitch = view.dessert_notification_switch
        val notificationTeaSwitch = view.notification_tea_switch
        val notificationSupperSwitch = view.notification_supper_switch
//        val notificationSnacksSwitch = view.notification_snacks_switch
//        val notificationTrainingSwitch = view.notification_training_switch

        val notificationBreakfastTime = view.breakfast_notification_time
        val notificationSecondBreakfastTime = view.second_breakfast_notification_time
        val notificationDinnerTime = view.dinner_notification_time
        val notificationDessertTime = view.dessert_notification_time
        val notificationTeaTime = view.tea_notification_time
        val notificationSupperTime = view.supper_notification_time
//        val notificationSnacksTime = view.snacks_notification_time
//        val notificationTrainingTime = view.training_notification_time

        //init auto suggest product list
        val autoSuggestProductListSwitch = view.autoSuggest_switch

        //set onClick for notification Time for get TImePicker
        notificationBreakfastTime.setOnClickListener(this)
        notificationSecondBreakfastTime.setOnClickListener(this)
        notificationDinnerTime.setOnClickListener(this)
        notificationDessertTime.setOnClickListener(this)
        notificationTeaTime.setOnClickListener(this)
        notificationSupperTime.setOnClickListener(this)
//        notificationSnacksTime.setOnClickListener(this)
//        notificationTrainingTime.setOnClickListener(this)

        //read settings from sharedPreferences
        val readPref = requireContext().getSharedPreferences(MAIN_PREF,0)
        kcal.setText("${readPref.getInt(PREF_KCAL,0)}")
        carbo.setText("${readPref.getInt(PREF_CARBO,0)}")
        fat.setText("${readPref.getInt(PREF_FAT,0)}")
        pro.setText("${readPref.getInt(PREF_PRO,0)}")

        breakfast.isChecked = readPref.getBoolean(PREF_MEAL_BREAKFAST,true)
        secondBreakfast.isChecked = readPref.getBoolean(PREF_MEAL_SECOND_BREAKFAST,true)
        dinner.isChecked = readPref.getBoolean(PREF_MEAL_DINNER,true)
        dessert.isChecked = readPref.getBoolean(PREF_MEAL_DESSERT,true)
        tea.isChecked = readPref.getBoolean(PREF_MEAL_TEA,true)
        supper.isChecked = readPref.getBoolean(PREF_MEAL_SUPPER,true)
        snacks.isChecked = readPref.getBoolean(PREF_MEAL_SNACKS,true)
        training.isChecked = readPref.getBoolean(PREF_MEAL_TRAINING,true)

        //turn on the switches based on the meals selected
        notificationBreakfastSwitch.isEnabled = breakfast.isChecked
        notificationSecondBreakfastSwitch.isEnabled = secondBreakfast.isChecked
        notificationDinnerSwitch.isEnabled = dinner.isChecked
        notificationDessertSwitch.isEnabled = dessert.isChecked
        notificationTeaSwitch.isEnabled = tea.isChecked
        notificationSupperSwitch.isEnabled = supper.isChecked
//        notificationSnacksSwitch.isEnabled = snacks.isChecked
//        notificationTrainingSwitch.isEnabled = training.isChecked

        //read notification state from sharedPreferences -> SWITCH & TIME
        notificationBreakfastSwitch.isChecked = readPref.getBoolean(PREF_BREAKFAST_NOTIFICATION,false)
        notificationSecondBreakfastSwitch.isChecked = readPref.getBoolean(PREF_SECOND_BREAKFAST_NOTIFICATION,false)
        notificationDinnerSwitch.isChecked = readPref.getBoolean(PREF_DINNER_NOTIFICATION,false)
        notificationDessertSwitch.isChecked = readPref.getBoolean(PREF_DESSERT_NOTIFICATION,false)
        notificationTeaSwitch.isChecked = readPref.getBoolean(PREF_TEA_NOTIFICATION,false)
        notificationSupperSwitch.isChecked = readPref.getBoolean(PREF_SUPPER_NOTIFICATION,false)
//        notificationSnacksSwitch.isChecked = readPref.getBoolean(PREF_SNACKS_NOTIFICATION,false)
//        notificationTrainingSwitch.isChecked = readPref.getBoolean(PREF_TRAINING_NOTIFICATION,false)

        //set time picker enables based on the notification swift state
        notificationBreakfastTime.isEnabled = notificationBreakfastSwitch.isChecked
        notificationSecondBreakfastTime.isEnabled = notificationSecondBreakfastSwitch.isChecked
        notificationDinnerTime.isEnabled = notificationDinnerSwitch.isChecked
        notificationDessertTime.isEnabled = notificationDessertSwitch.isChecked
        notificationTeaTime.isEnabled = notificationTeaSwitch.isChecked
        notificationSupperTime.isEnabled = notificationSupperSwitch.isChecked
//        notificationSnacksTime.isEnabled = notificationSnacksSwitch.isChecked
//        notificationTrainingTime.isEnabled = notificationTrainingSwitch.isChecked

        //set auto suggest meal list state
        autoSuggestProductListSwitch.isChecked = readPref.getBoolean(PREF_AUTO_SUGGEST_MEAL,true)

        notificationBreakfastTime.text = readPref.getString(PREF_BREAKFAST_NOTIFICATION_TIME,"00:00")
        notificationSecondBreakfastTime.text = readPref.getString(PREF_SECOND_BREAKFAST_NOTIFICATION_TIME,"00:00")
        notificationDinnerTime.text = readPref.getString(PREF_DINNER_NOTIFICATION_TIME,"00:00")
        notificationDessertTime.text = readPref.getString(PREF_DESSERT_NOTIFICATION_TIME,"00:00")
        notificationTeaTime.text = readPref.getString(PREF_TEA_NOTIFICATION_TIME,"00:00")
        notificationSupperTime.text = readPref.getString(PREF_SUPPER_NOTIFICATION_TIME,"00:00")
//        notificationSnacksTime.text = readPref.getString(PREF_SNACKS_NOTIFICATION_TIME,"00:00")
//        notificationTrainingTime.text = readPref.getString(PREF_TRAINING_NOTIFICATION_TIME,"00:00")

        //add changeListener to the notification switch -> change notification switch & time state based on the meals selected
        breakfast.setOnCheckedChangeListener { buttonView, isChecked ->
            notificationBreakfastSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
        }
        secondBreakfast.setOnCheckedChangeListener { buttonView, isChecked ->
            notificationSecondBreakfastSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
        }
        dinner.setOnCheckedChangeListener { buttonView, isChecked ->
            notificationDinnerSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
        }
        dessert.setOnCheckedChangeListener { buttonView, isChecked ->
            notificationDessertSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
        }
        tea.setOnCheckedChangeListener { buttonView, isChecked ->
            notificationTeaSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
        }
        supper.setOnCheckedChangeListener { buttonView, isChecked ->
            notificationSupperSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
        }
//        snacks.setOnCheckedChangeListener { buttonView, isChecked ->
//            notificationSnacksSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
//        }
//        training.setOnCheckedChangeListener { buttonView, isChecked ->
//            notificationTrainingSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
//        }

        notificationBreakfastSwitch.setOnCheckedChangeListener { buttonView, isChecked -> notificationBreakfastTime.isEnabled = isChecked }
        notificationSecondBreakfastSwitch.setOnCheckedChangeListener { buttonView, isChecked -> notificationSecondBreakfastTime.isEnabled = isChecked }
        notificationDinnerSwitch.setOnCheckedChangeListener { buttonView, isChecked -> notificationDinnerTime.isEnabled = isChecked }
        notificationDessertSwitch.setOnCheckedChangeListener { buttonView, isChecked -> notificationDessertTime.isEnabled = isChecked }
        notificationTeaSwitch.setOnCheckedChangeListener { buttonView, isChecked -> notificationTeaTime.isEnabled = isChecked }
        notificationSupperSwitch.setOnCheckedChangeListener { buttonView, isChecked -> notificationSupperTime.isEnabled = isChecked }
//        notificationSnacksSwitch.setOnCheckedChangeListener { buttonView, isChecked -> notificationSnacksTime.isEnabled = isChecked }
//        notificationTrainingSwitch.setOnCheckedChangeListener { buttonView, isChecked -> notificationTrainingTime.isEnabled = isChecked }

        //save settings button
        saveBt = view.findViewById<Button>(R.id.ok_product_settings_dialog)
        saveBt.setOnClickListener{

            val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(MAIN_PREF, 0 )
            val edit = sharedPreferences.edit()
            val dbManager = MyDatabaseHelper(requireContext())

            edit.putInt(PREF_KCAL, kcal.text.toString().toInt())
            edit.putInt(PREF_CARBO, carbo.text.toString().toInt())
            edit.putInt(PREF_FAT,fat.text.toString().toInt())
            edit.putInt(PREF_PRO,pro.text.toString().toInt())

            edit.putBoolean(PREF_MEAL_BREAKFAST,breakfast.isChecked)
            edit.putBoolean(PREF_MEAL_SECOND_BREAKFAST,secondBreakfast.isChecked)
            edit.putBoolean(PREF_MEAL_DINNER,dinner.isChecked)
            edit.putBoolean(PREF_MEAL_DESSERT,dessert.isChecked)
            edit.putBoolean(PREF_MEAL_TEA,tea.isChecked)
            edit.putBoolean(PREF_MEAL_SUPPER,supper.isChecked)
            edit.putBoolean(PREF_MEAL_SNACKS,snacks.isChecked)
            edit.putBoolean(PREF_MEAL_TRAINING,training.isChecked)

            edit.putBoolean(PREF_BREAKFAST_NOTIFICATION,notificationBreakfastSwitch.isChecked)
            edit.putBoolean(PREF_SECOND_BREAKFAST_NOTIFICATION,notificationSecondBreakfastSwitch.isChecked)
            edit.putBoolean(PREF_DINNER_NOTIFICATION,notificationDinnerSwitch.isChecked)
            edit.putBoolean(PREF_DESSERT_NOTIFICATION, notificationDessertSwitch.isChecked)
            edit.putBoolean(PREF_TEA_NOTIFICATION,notificationTeaSwitch.isChecked)
            edit.putBoolean(PREF_SUPPER_NOTIFICATION,notificationSupperSwitch.isChecked)
//            edit.putBoolean(PREF_SNACKS_NOTIFICATION,notificationSnacksSwitch.isChecked)
//            edit.putBoolean(PREF_TRAINING_NOTIFICATION,notificationTrainingSwitch.isChecked)

            edit.putString(PREF_BREAKFAST_NOTIFICATION_TIME,notificationBreakfastTime.text.toString())
            edit.putString(PREF_SECOND_BREAKFAST_NOTIFICATION_TIME,notificationSecondBreakfastTime.text.toString())
            edit.putString(PREF_DINNER_NOTIFICATION_TIME,notificationDinnerTime.text.toString())
            edit.putString(PREF_DESSERT_NOTIFICATION_TIME,notificationDessertTime.text.toString())
            edit.putString(PREF_TEA_NOTIFICATION_TIME,notificationTeaTime.text.toString())
            edit.putString(PREF_SUPPER_NOTIFICATION_TIME,notificationSupperTime.text.toString())
//            edit.putString(PREF_SNACKS_NOTIFICATION_TIME,notificationSnacksTime.text.toString())
//            edit.putString(PREF_TRAINING_NOTIFICATION_TIME,notificationTrainingTime.text.toString())

            edit.putBoolean(PREF_AUTO_SUGGEST_MEAL, autoSuggestProductListSwitch.isChecked)

            edit.apply()

            setupNotificationAlarm(notificationBreakfastTime.text.toString(),0,notificationBreakfastSwitch.isChecked)
            setupNotificationAlarm(notificationSecondBreakfastTime.text.toString(),1,notificationSecondBreakfastSwitch.isChecked)
            setupNotificationAlarm(notificationDinnerTime.text.toString(),2,notificationDinnerSwitch.isChecked)
            setupNotificationAlarm(notificationDessertTime.text.toString(),3,notificationDessertSwitch.isChecked)
            setupNotificationAlarm(notificationTeaTime.text.toString(),4,notificationTeaSwitch.isChecked)
            setupNotificationAlarm(notificationSupperTime.text.toString(),5,notificationSupperSwitch.isChecked)
//            setupNotificationAlarm(notificationSnacksTime.text.toString(),6,notificationSnacksSwitch.isChecked)
//            setupNotificationAlarm(notificationTrainingTime.text.toString(),7,notificationTrainingSwitch.isChecked)

            dbManager.updateDailyGoalNutrients(date.toString(),
                kcal.text.toString().toInt(),
                fat.text.toString().toInt(),
                carbo.text.toString().toInt(),
                pro.text.toString().toInt()
            )

            Toast.makeText(requireContext(), getString(R.string.data_has_been_saved), Toast.LENGTH_SHORT).show()

        }

        clearUserHoises.setOnLongClickListener {
            val dbManager = MyDatabaseHelper(requireContext())
            dbManager.deleteUserHoses()
        }

        return view
    }

    private fun setupNotificationAlarm(time: String, broadcast: Int , status: Boolean) {

        val hour = time.subSequence(0,2)
        val min = time.subSequence(3,5)

        val calender: Calendar = Calendar.getInstance()
        calender.set(Calendar.HOUR_OF_DAY,hour.toString().toInt())
        calender.set(Calendar.MINUTE,min.toString().toInt())

        val alarmManager: AlarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent()

        when(broadcast){
            0 -> intent = Intent(context,ReminderBroadcastBreakfast::class.java)
            1 -> intent = Intent(context,ReminderBroadcastSecondBreakfast::class.java)
            2 -> intent = Intent(context,ReminderBroadcastDinner::class.java)
            3 -> intent = Intent(context,ReminderBroadcastDessert::class.java)
            4 -> intent = Intent(context,ReminderBroadcastTea::class.java)
            5 -> intent = Intent(context,ReminderBroadcastSupper::class.java)
//            6 -> intent = Intent(context,ReminderBroadcastSnacks::class.java)
//            7 -> intent = Intent(context,ReminderBroadcastTraining::class.java)
        }
        val pendingIntent = PendingIntent.getBroadcast(context,broadcast,intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (calender.before(Calendar.getInstance())){
            calender.add(Calendar.DATE,1)
        }

        if (!status){
            alarmManager.cancel(pendingIntent)
        }else{
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calender.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent)
        }
    }

    override fun onClick(v: View?) {

        v as TextView
        var requestCode: Int = 0
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)

            //set time to the notificationTime TextView
            when(v){

                v.breakfast_notification_time -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
                v.second_breakfast_notification_time -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
                v.dinner_notification_time -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
                v.dessert_notification_time -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
                v.tea_notification_time -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
                v.supper_notification_time -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
//                v.snacks_notification_time -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
//                v.training_notification_time -> v.text = SimpleDateFormat("HH:mm").format(cal.time)


            }

            when(v){
                v.breakfast_notification_time -> requestCode = 0
                v.second_breakfast_notification_time -> requestCode = 1
                v.dinner_notification_time -> requestCode = 2
                v.dessert_notification_time -> requestCode = 3
                v.tea_notification_time -> requestCode = 4
                v.supper_notification_time -> requestCode = 5
//                v.snacks_notification_time -> requestCode = 6
//                v.training_notification_time -> requestCode = 7
            }
        }

        TimePickerDialog(requireContext(),timeSetListener,cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),true).show()

    }

    override fun onPause() {
        super.onPause()

//        saveBt.callOnClick() //auto save setting when user exit from settings
    }
}
