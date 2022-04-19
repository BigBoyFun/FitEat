package com.policinski.dev.fiteat

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.policinski.dev.fiteat.databinding.FragmentSettingsFragmentBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.math.ceil

class SettingsFragment : Fragment(), View.OnClickListener {

    private var _binding : FragmentSettingsFragmentBinding? = null
    private val binding get() = _binding!!
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

    val maxPercent = 100.0
    val fatMultiplier = 9.0
    val carboMultiplier = 4.0
    val proteinMultiplier = 4.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsFragmentBinding.inflate(inflater,container,false)

        var view = binding.root

        view?.context?.getSharedPreferences(MAIN_PREF,0)
            ?.edit()?.putString(PREF_CURRENTLY_VIEWED_LIST,"All")?.apply()

        val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        //set onClick for notification Time for get TImePicker
        binding.breakfastNotificationTime.setOnClickListener(this)
        binding.secondBreakfastNotificationTime.setOnClickListener(this)
        binding.dinnerNotificationTime.setOnClickListener(this)
        binding.dessertNotificationTime.setOnClickListener(this)
        binding.teaNotificationTime.setOnClickListener(this)
        binding.supperNotificationTime.setOnClickListener(this)
//        notificationSnacksTime.setOnClickListener(this)
//        notificationTrainingTime.setOnClickListener(this)

        //read settings from sharedPreferences
        val readPref = requireContext().getSharedPreferences(MAIN_PREF,0)
        binding.kcalProductSettingsDialog.setText("${readPref.getInt(PREF_KCAL,2100)}")
        binding.carboUserProductSettingsDialog.setText("${readPref.getInt(PREF_CARBO,262)}")
        binding.fatUserProductSettingsDialog.setText("${readPref.getInt(PREF_FAT,46)}")
        binding.proUserProductSettingsDialog.setText("${readPref.getInt(PREF_PRO,157)}")

        var editKcal = binding.kcalProductSettingsDialog.text.toString().toDouble()
        var editCarbo = binding.carboUserProductSettingsDialog.text.toString().toDouble()
        var editFat = binding.fatUserProductSettingsDialog.text.toString().toDouble()
        var editProtein = binding.proUserProductSettingsDialog.text.toString().toDouble()

        //calculate nutrients goal in percent
        binding.setFatInPercent.setText("${calculatePercent(binding.fatUserProductSettingsDialog.text.toString().toDouble() * fatMultiplier,binding.kcalProductSettingsDialog.text.toString().toDouble())}")
        binding.setCarboInPercent.setText("${calculatePercent(binding.carboUserProductSettingsDialog.text.toString().toDouble() * carboMultiplier,binding.kcalProductSettingsDialog.text.toString().toDouble())}")
        binding.setProteinInPercent.setText("${calculatePercent(binding.proUserProductSettingsDialog.text.toString().toDouble() * proteinMultiplier,binding.kcalProductSettingsDialog.text.toString().toDouble())}")

        var editFatInPercent = binding.setFatInPercent.text.toString().toDouble()
        var editCarboInPercent = binding.setCarboInPercent.text.toString().toDouble()
        var editProteinPercent = binding.setProteinInPercent.text.toString().toDouble()
        var leftPercent = maxPercent - editFatInPercent - editProteinPercent - editCarboInPercent

        binding.leftPercentTv.text = leftPercent.toString()

        fun calculateNutrientsFromPercent(kcal: Int){
            editCarbo = (editCarboInPercent / 100) * kcal / carboMultiplier
            binding.carboUserProductSettingsDialog.setText("${editCarbo.toInt()}")
            editFat = (editFatInPercent / 100) * kcal / fatMultiplier
            binding.fatUserProductSettingsDialog.setText("${editFat.toInt()}")
            editProtein = (editProteinPercent / 100) * kcal / proteinMultiplier
            binding.proUserProductSettingsDialog.setText("${editProtein.toInt()}")
        }
//

        binding.setProteinInPercent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.setProteinInPercent.isFocused) {
                    if (p0?.isNotEmpty() == true) {
                        editProteinPercent = p0.toString().toDouble()
                        leftPercent = maxPercent - editFatInPercent - editProteinPercent - editCarboInPercent
                        binding.leftPercentTv.text = leftPercent.toString()
                        if (maxPercent - editProteinPercent - editCarboInPercent - editFatInPercent >= 0.0) {
                            binding.proUserProductSettingsDialog.setText(
                                "${
                                    ((p0.toString().toDouble() / 100.0) * editKcal / proteinMultiplier).toInt()}"
                            )
                        } else if (maxPercent - editProteinPercent - editCarboInPercent - editFatInPercent < 0.0) {
                            Toast.makeText(requireContext(), "ERROR PROTEIN PERCENT", Toast.LENGTH_SHORT).show()
                            binding.setProteinInPercent.text.clear()
                            binding.proUserProductSettingsDialog.text!!.clear()
                        }
                    } else {
                        editProteinPercent = 0.0
                        editProtein = 0.0
                        leftPercent = maxPercent - editFatInPercent - editProteinPercent - editCarboInPercent
                        binding.leftPercentTv.text = leftPercent.toString()
                        binding.proUserProductSettingsDialog.text!!.clear()
                    }
                }
            }


            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.setCarboInPercent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.setCarboInPercent.isFocused) {
                    if (p0?.isNotEmpty() == true) {
                        editCarboInPercent = p0.toString().toDouble()
                        leftPercent = maxPercent - editFatInPercent - editProteinPercent - editCarboInPercent
                        binding.leftPercentTv.text = leftPercent.toString()
                        if (maxPercent - editProteinPercent - editCarboInPercent - editFatInPercent >= 0.0) {
                            binding.carboUserProductSettingsDialog.setText(
                                "${
                                    ((p0.toString()
                                        .toDouble() / 100.0) * editKcal / carboMultiplier).toInt()
                                }"
                            )
                        } else if (maxPercent - editProteinPercent - editCarboInPercent - editFatInPercent < 0.0) {
                            Toast.makeText(requireContext(), "ERROR CARBO PERCENT", Toast.LENGTH_SHORT).show()
                            binding.setCarboInPercent.text.clear()
                            binding.carboUserProductSettingsDialog.text!!.clear()
                        }
                    } else {
                        editCarboInPercent = 0.0
                        editCarbo = 0.0
                        leftPercent = maxPercent - editFatInPercent - editProteinPercent - editCarboInPercent
                        binding.leftPercentTv.text = leftPercent.toString()
                        binding.carboUserProductSettingsDialog.text!!.clear()
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.setFatInPercent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.setFatInPercent.isFocused) {
                    if (p0?.isNotEmpty() == true) {
                        editFatInPercent = p0.toString().toDouble()
                        leftPercent = maxPercent - editFatInPercent - editProteinPercent - editCarboInPercent
                        binding.leftPercentTv.text = leftPercent.toString()
                        if (maxPercent - editProteinPercent - editCarboInPercent - editFatInPercent >= 0.0) {
                            binding.fatUserProductSettingsDialog.setText(
                                "${
                                    ((p0.toString()
                                        .toDouble() / 100.0) * editKcal / fatMultiplier).toInt()
                                }"
                            )
                        } else if (maxPercent - editProteinPercent - editCarboInPercent - editFatInPercent < 0.0) {
                            Toast.makeText(requireContext(), "ERROR FAT PERCENT", Toast.LENGTH_SHORT).show()
                            binding.setFatInPercent.text.clear()
                            binding.fatUserProductSettingsDialog.text!!.clear()
                        }
                    } else {
                        editFatInPercent = 0.0
                        editFat = 0.0
                        leftPercent = maxPercent - editFatInPercent - editProteinPercent - editCarboInPercent
                        binding.leftPercentTv.text = leftPercent.toString()
                        binding.fatUserProductSettingsDialog.text!!.clear()
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.carboUserProductSettingsDialog.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (binding.carboUserProductSettingsDialog.isFocused) {
                    if (p0?.isNotEmpty() == true && (p0.toString()
                            .toDouble() * carboMultiplier) + (editFat * fatMultiplier) + (editProtein * proteinMultiplier) <= editKcal
                    ) {
                        editCarbo = p0.toString().toDouble()
                        editCarboInPercent = calculatePercent(
                            p0.toString().toDouble() * carboMultiplier,
                            editKcal
                        ).toDouble()
                        binding.setCarboInPercent.setText("${editCarboInPercent.toInt()}")
                        binding.leftPercentTv.text = "${maxPercent - (editCarboInPercent + editProteinPercent + editFatInPercent)}"
                    } else if (((if (p0?.isNotEmpty() == true) p0.toString().toDouble() else 0.0) * carboMultiplier) + (editFat * fatMultiplier) + (editProtein * proteinMultiplier) > editKcal || p0?.isNotEmpty() == false
                    ){
                        binding.setCarboInPercent.text.clear()
                        binding.setCarboInPercent.text.clear()
                        editCarboInPercent = 0.0
                        editCarbo = 0.0
                        binding.leftPercentTv.text = "${maxPercent - (editCarboInPercent + editProteinPercent + editFatInPercent)}"
                    }
                } else {
                    //do nothing
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.fatUserProductSettingsDialog.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.fatUserProductSettingsDialog.isFocused) {
                    if (p0?.isNotEmpty() == true && (p0.toString()
                            .toDouble() * fatMultiplier) + (editCarbo * carboMultiplier) + (editProtein * proteinMultiplier) <= editKcal
                    ) {
                        editFat = p0.toString().toDouble()
                        editFatInPercent = calculatePercent(
                            p0.toString().toDouble() * fatMultiplier,
                            editKcal
                        ).toDouble()
                        binding.setFatInPercent.setText("${editFatInPercent.toInt()}")
                        binding.leftPercentTv.text = "${maxPercent - (editCarboInPercent + editProteinPercent + editFatInPercent)}"
                    } else if (((if (p0?.isNotEmpty() == true) p0.toString().toDouble() else 0.0) * fatMultiplier) + (editCarbo * carboMultiplier) + (editProtein * proteinMultiplier) > editKcal || p0?.isNotEmpty() == false) {
                        binding.fatUserProductSettingsDialog.text!!.clear()
                        binding.setFatInPercent.text.clear()
                        editFatInPercent = 0.0
                        editFat = 0.0
                        binding.leftPercentTv.text = "${maxPercent - (editCarboInPercent + editProteinPercent + editFatInPercent)}"
                    }
                } else {
                    //do nothing
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.proUserProductSettingsDialog.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.proUserProductSettingsDialog.isFocused) {
                    if (p0?.isNotEmpty() == true && (p0.toString()
                            .toDouble() * proteinMultiplier) + (editFat * fatMultiplier) + (editCarbo * carboMultiplier) <= editKcal
                    ) {
                        editProtein = p0.toString().toDouble()
                        editProteinPercent = calculatePercent(
                            p0.toString().toDouble() * proteinMultiplier,
                            editKcal
                        ).toDouble()
                        binding.setProteinInPercent.setText("${editProteinPercent.toInt()}")
                        binding.leftPercentTv.text = "${maxPercent - (editCarboInPercent + editProteinPercent + editFatInPercent)}"
                    } else if (((if (p0?.isNotEmpty() == true) p0.toString().toDouble() else 0.0) * proteinMultiplier) + (editFat * fatMultiplier) + (editCarbo * carboMultiplier) > editKcal || p0?.isNotEmpty() == false)  {
                        binding.proUserProductSettingsDialog.text!!.clear()
                        binding.setProteinInPercent.text.clear()
                        editProteinPercent = 0.0
                        editProtein = 0.0
                        binding.leftPercentTv.text = "${maxPercent - (editCarboInPercent + editProteinPercent + editFatInPercent)}"
                    }
                } else {
                    //do nothing
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.kcalProductSettingsDialog.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.kcalProductSettingsDialog.text!!.isNotEmpty()) {
                    calculateNutrientsFromPercent(p0.toString().toInt())
                } else {
                    binding.fatUserProductSettingsDialog.setText("")
                    binding.carboUserProductSettingsDialog.setText("")
                    binding.proUserProductSettingsDialog.setText("")
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.breakfastCheckBox.isChecked = readPref.getBoolean(PREF_MEAL_BREAKFAST,true)
        binding.secondBreakfastCheckBox.isChecked = readPref.getBoolean(PREF_MEAL_SECOND_BREAKFAST,true)
        binding.dinnerCheckBox.isChecked = readPref.getBoolean(PREF_MEAL_DINNER,true)
        binding.dessertCheckBox.isChecked = readPref.getBoolean(PREF_MEAL_DESSERT,true)
        binding.teaCheckBox.isChecked = readPref.getBoolean(PREF_MEAL_TEA,true)
        binding.supperCheckBox.isChecked = readPref.getBoolean(PREF_MEAL_SUPPER,true)
        binding.snacksCheckBox.isChecked = readPref.getBoolean(PREF_MEAL_SNACKS,true)
        binding.trainingCheckBox.isChecked = readPref.getBoolean(PREF_MEAL_TRAINING,true)

        //turn on the switches based on the meals selected
        binding.notificationBreakfastSwitch.isEnabled = binding.breakfastCheckBox.isChecked
        binding.secondBreakfastNotificationSwitch.isEnabled = binding.secondBreakfastCheckBox.isChecked
        binding.dinnerNotificationSwitch.isEnabled = binding.dinnerCheckBox.isChecked
        binding.dessertNotificationSwitch.isEnabled = binding.dessertCheckBox.isChecked
        binding.notificationTeaSwitch.isEnabled = binding.teaCheckBox.isChecked
        binding.notificationSupperSwitch.isEnabled = binding.supperCheckBox.isChecked
//        notificationSnacksSwitch.isEnabled = snacks.isChecked
//        notificationTrainingSwitch.isEnabled = training.isChecked

        //read notification state from sharedPreferences -> SWITCH & TIME
        binding.notificationBreakfastSwitch.isChecked = readPref.getBoolean(PREF_BREAKFAST_NOTIFICATION,false)
        binding.secondBreakfastNotificationSwitch.isChecked = readPref.getBoolean(PREF_SECOND_BREAKFAST_NOTIFICATION,false)
        binding.dinnerNotificationSwitch.isChecked = readPref.getBoolean(PREF_DINNER_NOTIFICATION,false)
        binding.dessertNotificationSwitch.isChecked = readPref.getBoolean(PREF_DESSERT_NOTIFICATION,false)
        binding.notificationTeaSwitch.isChecked = readPref.getBoolean(PREF_TEA_NOTIFICATION,false)
        binding.notificationSupperSwitch.isChecked = readPref.getBoolean(PREF_SUPPER_NOTIFICATION,false)
//        notificationSnacksSwitch.isChecked = readPref.getBoolean(PREF_SNACKS_NOTIFICATION,false)
//        notificationTrainingSwitch.isChecked = readPref.getBoolean(PREF_TRAINING_NOTIFICATION,false)

        //set time picker enables based on the notification swift state
        binding.breakfastNotificationTime.isEnabled = binding.notificationBreakfastSwitch.isChecked
        binding.secondBreakfastNotificationTime.isEnabled = binding.secondBreakfastNotificationSwitch.isChecked
        binding.dinnerNotificationTime.isEnabled = binding.dinnerNotificationSwitch.isChecked
        binding.dessertNotificationTime.isEnabled = binding.dessertNotificationSwitch.isChecked
        binding.teaNotificationTime.isEnabled = binding.notificationTeaSwitch.isChecked
        binding.supperNotificationTime.isEnabled = binding.notificationSupperSwitch.isChecked
//        notificationSnacksTime.isEnabled = notificationSnacksSwitch.isChecked
//        notificationTrainingTime.isEnabled = notificationTrainingSwitch.isChecked

        //set auto suggest meal list state
        binding.autoSuggestSwitch.isChecked = readPref.getBoolean(PREF_AUTO_SUGGEST_MEAL,true)

        binding.breakfastNotificationTime.text = readPref.getString(PREF_BREAKFAST_NOTIFICATION_TIME,"00:00")
        binding.secondBreakfastNotificationTime.text = readPref.getString(PREF_SECOND_BREAKFAST_NOTIFICATION_TIME,"00:00")
        binding.dinnerNotificationTime.text = readPref.getString(PREF_DINNER_NOTIFICATION_TIME,"00:00")
        binding.dessertNotificationTime.text = readPref.getString(PREF_DESSERT_NOTIFICATION_TIME,"00:00")
        binding.teaNotificationTime.text = readPref.getString(PREF_TEA_NOTIFICATION_TIME,"00:00")
        binding.supperNotificationTime.text = readPref.getString(PREF_SUPPER_NOTIFICATION_TIME,"00:00")
//        notificationSnacksTime.text = readPref.getString(PREF_SNACKS_NOTIFICATION_TIME,"00:00")
//        notificationTrainingTime.text = readPref.getString(PREF_TRAINING_NOTIFICATION_TIME,"00:00")

        //add changeListener to the notification switch -> change notification switch & time state based on the meals selected
        binding.breakfastCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.notificationBreakfastSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
        }
        binding.secondBreakfastCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.secondBreakfastNotificationSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
        }
        binding.dinnerCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.dinnerNotificationSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
        }
        binding.dessertCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.dessertNotificationSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
        }
        binding.teaCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.notificationTeaSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
        }
        binding.supperCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.notificationSupperSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
        }
//        snacks.setOnCheckedChangeListener { buttonView, isChecked ->
//            notificationSnacksSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
//        }
//        training.setOnCheckedChangeListener { buttonView, isChecked ->
//            notificationTrainingSwitch.apply { this.isChecked = false; this.isEnabled = isChecked }
//        }

        binding.notificationBreakfastSwitch.setOnCheckedChangeListener { buttonView, isChecked -> binding.breakfastNotificationTime.isEnabled = isChecked }
        binding.secondBreakfastNotificationSwitch.setOnCheckedChangeListener { buttonView, isChecked -> binding.secondBreakfastNotificationTime.isEnabled = isChecked }
        binding.dinnerNotificationSwitch.setOnCheckedChangeListener { buttonView, isChecked -> binding.dinnerNotificationTime.isEnabled = isChecked }
        binding.dessertNotificationSwitch.setOnCheckedChangeListener { buttonView, isChecked -> binding.dessertNotificationTime.isEnabled = isChecked }
        binding.notificationTeaSwitch.setOnCheckedChangeListener { buttonView, isChecked -> binding.teaNotificationTime.isEnabled = isChecked }
        binding.notificationSupperSwitch.setOnCheckedChangeListener { buttonView, isChecked -> binding.supperNotificationTime.isEnabled = isChecked }
//        notificationSnacksSwitch.setOnCheckedChangeListener { buttonView, isChecked -> notificationSnacksTime.isEnabled = isChecked }
//        notificationTrainingSwitch.setOnCheckedChangeListener { buttonView, isChecked -> notificationTrainingTime.isEnabled = isChecked }

        //save settings button
        binding.okProductSettingsDialog.setOnClickListener{

            val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(MAIN_PREF, 0 )
            val edit = sharedPreferences.edit()
            val dbManager = MyDatabaseHelper(requireContext())

            val sortTimeList = mutableListOf<String>()
            sortTimeList.add(binding.breakfastNotificationTime.text.toString())
            sortTimeList.add(binding.secondBreakfastNotificationTime.text.toString())
            sortTimeList.add(binding.dinnerNotificationTime.text.toString())
            sortTimeList.add(binding.dessertNotificationTime.text.toString())
            sortTimeList.add(binding.teaNotificationTime.text.toString())
            sortTimeList.add(binding.supperNotificationTime.text.toString())

            val stateMealList =  mutableListOf<Boolean>()
            stateMealList.add(binding.breakfastCheckBox.isChecked)
            stateMealList.add(binding.secondBreakfastCheckBox.isChecked)
            stateMealList.add(binding.dinnerCheckBox.isChecked)
            stateMealList.add(binding.dessertCheckBox.isChecked)
            stateMealList.add(binding.teaCheckBox.isChecked)
            stateMealList.add(binding.supperCheckBox.isChecked)


            //Start
            /*This block is responsible for the correct order of eaten meals
            (meal times must be in the correct order with the generally accepted order of meals) */
            for( (index, state) in stateMealList.withIndex()) {
                if(!state){
                    sortTimeList[index] = "99:99"
                }
            }

            sortTimeList.sortBy { (it.substring(0,2) + it.substring(3,5)).toInt() } //format hour to int for make time in correct order ( 12:54 h -> 1254 Int )

            //Create a list with the correct sequence of alarms, if current meal is inactive then his alarm time is set to 00:00
            var itListTime = 0
            var sortedTimeForSave = mutableListOf<String>()
            for ( state in stateMealList ) {
                if (state) {
                    sortedTimeForSave.add(sortTimeList[itListTime])
                    itListTime++
                } else {
                    sortedTimeForSave.add("00:00")
                }
            }
            //End

            edit.putInt(PREF_KCAL, binding.kcalProductSettingsDialog.text.toString().toInt())
            edit.putInt(PREF_CARBO, binding.carboUserProductSettingsDialog.text.toString().toInt())
            edit.putInt(PREF_FAT, binding.fatUserProductSettingsDialog.text.toString().toInt())
            edit.putInt(PREF_PRO, binding.proUserProductSettingsDialog.text.toString().toInt())

            edit.putBoolean(PREF_MEAL_BREAKFAST, binding.breakfastCheckBox.isChecked)
            edit.putBoolean(PREF_MEAL_SECOND_BREAKFAST, binding.secondBreakfastCheckBox.isChecked)
            edit.putBoolean(PREF_MEAL_DINNER, binding.dinnerCheckBox.isChecked)
            edit.putBoolean(PREF_MEAL_DESSERT, binding.dessertCheckBox.isChecked)
            edit.putBoolean(PREF_MEAL_TEA, binding.teaCheckBox.isChecked)
            edit.putBoolean(PREF_MEAL_SUPPER, binding.supperCheckBox.isChecked)
            edit.putBoolean(PREF_MEAL_SNACKS, binding.snacksCheckBox.isChecked)
            edit.putBoolean(PREF_MEAL_TRAINING, binding.trainingCheckBox.isChecked)

            edit.putBoolean(PREF_BREAKFAST_NOTIFICATION, binding.notificationBreakfastSwitch.isChecked)
            edit.putBoolean(PREF_SECOND_BREAKFAST_NOTIFICATION, binding.secondBreakfastNotificationSwitch.isChecked)
            edit.putBoolean(PREF_DINNER_NOTIFICATION, binding.dinnerNotificationSwitch.isChecked)
            edit.putBoolean(PREF_DESSERT_NOTIFICATION, binding.dessertNotificationSwitch.isChecked)
            edit.putBoolean(PREF_TEA_NOTIFICATION, binding.notificationTeaSwitch.isChecked)
            edit.putBoolean(PREF_SUPPER_NOTIFICATION, binding.notificationSupperSwitch.isChecked)
//            edit.putBoolean(PREF_SNACKS_NOTIFICATION,notificationSnacksSwitch.isChecked)
//            edit.putBoolean(PREF_TRAINING_NOTIFICATION,notificationTrainingSwitch.isChecked)

            edit.putString(PREF_BREAKFAST_NOTIFICATION_TIME, sortedTimeForSave[0])
            edit.putString(PREF_SECOND_BREAKFAST_NOTIFICATION_TIME, sortedTimeForSave[1])
            edit.putString(PREF_DINNER_NOTIFICATION_TIME, sortedTimeForSave[2])
            edit.putString(PREF_DESSERT_NOTIFICATION_TIME, sortedTimeForSave[3])
            edit.putString(PREF_TEA_NOTIFICATION_TIME, sortedTimeForSave[4])
            edit.putString(PREF_SUPPER_NOTIFICATION_TIME, sortedTimeForSave[5])
//            edit.putString(PREF_SNACKS_NOTIFICATION_TIME,notificationSnacksTime.text.toString())
//            edit.putString(PREF_TRAINING_NOTIFICATION_TIME,notificationTrainingTime.text.toString())

            edit.putBoolean(PREF_AUTO_SUGGEST_MEAL, binding.autoSuggestSwitch.isChecked)

            edit.apply()

            setupNotificationAlarm(binding.breakfastNotificationTime.text.toString(),0, binding.notificationBreakfastSwitch.isChecked)
            setupNotificationAlarm(binding.secondBreakfastNotificationTime.text.toString(),1, binding.secondBreakfastNotificationSwitch.isChecked)
            setupNotificationAlarm(binding.dinnerNotificationTime.text.toString(),2, binding.dinnerNotificationSwitch.isChecked)
            setupNotificationAlarm(binding.dessertNotificationTime.text.toString(),3, binding.dessertNotificationSwitch.isChecked)
            setupNotificationAlarm(binding.teaNotificationTime.text.toString(),4, binding.notificationTeaSwitch.isChecked)
            setupNotificationAlarm(binding.supperNotificationTime.text.toString(),5, binding.notificationSupperSwitch.isChecked)
//            setupNotificationAlarm(notificationSnacksTime.text.toString(),6,notificationSnacksSwitch.isChecked)
//            setupNotificationAlarm(notificationTrainingTime.text.toString(),7,notificationTrainingSwitch.isChecked)

            dbManager.updateDailyGoalNutrients(date.toString(),
                binding.kcalProductSettingsDialog.text.toString().toInt(),
                binding.fatUserProductSettingsDialog.text.toString().toInt(),
                binding.carboUserProductSettingsDialog.text.toString().toInt(),
                binding.proUserProductSettingsDialog.text.toString().toInt()
            )

            Toast.makeText(requireContext(), getString(R.string.data_has_been_saved), Toast.LENGTH_SHORT).show()

        }

        binding.btClearUserHoises.setOnLongClickListener {
            val dbManager = MyDatabaseHelper(requireContext())
            dbManager.deleteUserHoses()
        }

        return view
    }

    private fun calculatePercent(quantity: Double, kcal: Double): Int {
        return ceil(quantity / (kcal * 0.01)).toInt()
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

                binding.breakfastNotificationTime -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
                binding.secondBreakfastNotificationTime -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
                binding.dinnerNotificationTime -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
                binding.dessertNotificationTime -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
                binding.teaNotificationTime -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
                binding.supperNotificationTime -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
//                v.snacks_notification_time -> v.text = SimpleDateFormat("HH:mm").format(cal.time)
//                v.training_notification_time -> v.text = SimpleDateFormat("HH:mm").format(cal.time)


            }

            when(v){
                binding.breakfastNotificationTime -> requestCode = 0
                binding.secondBreakfastNotificationTime -> requestCode = 1
                binding.dinnerNotificationTime -> requestCode = 2
                binding.dessertNotificationTime -> requestCode = 3
                binding.teaNotificationTime -> requestCode = 4
                binding.supperNotificationTime -> requestCode = 5
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
