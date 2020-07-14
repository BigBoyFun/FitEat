package com.policinski.dev.fiteat

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import java.time.LocalDate

class settings_fragment : Fragment() {

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_scanner_fragment, container, false)

        val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        //init all views from settings fragment
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

        //save settings button
        val saveBt = view.findViewById<Button>(R.id.ok_product_settings_dialog)
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
            edit.apply()

            dbManager.updateDailyGoalNutrients(date.toString(),
                kcal.text.toString().toInt(),
                fat.text.toString().toInt(),
                carbo.text.toString().toInt(),
                pro.text.toString().toInt()
            )

            Toast.makeText(requireContext(), getString(R.string.data_has_been_saved), Toast.LENGTH_SHORT).show()

        }

        return view
    }

}
