package com.policinski.dev.fiteat

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.time.LocalDate

class scanner_fragment : Fragment() {

    private val MAIN_PREF = "MAIN_PREF"
    private val PREF_CARBO = "PREF_CARBO"
    private val PREF_FAT = "PREF_FAT"
    private val PREF_KCAL = "PREF_KCAL"
    private val PREF_PRO = "PREF_PRO"


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

        var kcal = view.findViewById<EditText>(R.id.kcal_product_settings_dialog)
        var carbo = view.findViewById<EditText>(R.id.carbo_user_product_settings_dialog)
        var fat = view.findViewById<EditText>(R.id.fat_user_product_settings_dialog)
        var pro = view.findViewById<EditText>(R.id.pro_user_product_settings_dialog)

        val readPref = requireContext().getSharedPreferences(MAIN_PREF,0)
        kcal.setText("${readPref.getInt(PREF_KCAL,0)}")
        carbo.setText("${readPref.getInt(PREF_CARBO,0)}")
        fat.setText("${readPref.getInt(PREF_FAT,0)}")
        pro.setText("${readPref.getInt(PREF_PRO,0)}")


        val saveBt = view.findViewById<Button>(R.id.ok_product_settings_dialog)
        saveBt.setOnClickListener{

            val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences(MAIN_PREF, 0 )
            val edit = sharedPreferences.edit()
            val dbManager = MyDatabaseHelper(requireContext())

            edit.putInt(PREF_KCAL, kcal.text.toString().toInt())
            edit.putInt(PREF_CARBO, carbo.text.toString().toInt())
            edit.putInt(PREF_FAT,fat.text.toString().toInt())
            edit.putInt(PREF_PRO,pro.text.toString().toInt())
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
