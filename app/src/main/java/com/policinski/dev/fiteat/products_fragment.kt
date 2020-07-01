package com.policinski.dev.fiteat

import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.add_dialog_layout.*
import kotlinx.android.synthetic.main.fragment_products_fragment.view.*
import kotlinx.android.synthetic.main.fragment_products_fragment.view.current_kcal
import java.time.LocalDate

class products_fragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var adapterProduct: MyAdapter
    private lateinit var productList: MutableList<Product>
    private val MAIN_PREF = "MAIN_PREF"
    private val PREF_CARBO = "PREF_CARBO"
    private val PREF_FAT = "PREF_FAT"
    private val PREF_KCAL = "PREF_KCAL"
    private val PREF_PRO = "PREF_PRO"
    private var current_kcal = 0
    private var current_pro = 0.0
    private var current_fat = 0.0
    private var current_carbo = 0.0
    lateinit var date: Any

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val dbManager = MyDatabaseHelper(requireContext())

        date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {

        }

        productList = mutableListOf()

        val cursor = dbManager.readAllData()

        if (cursor.moveToNext()){

            var name: String
            var kcal: Int
            var fat: Double
            var protein: Double
            var carbo: Double
            var weight: Int
            var favorite: Int
            var id: Int

            do{

                name = cursor.getString(cursor.getColumnIndex("Name"))
                kcal = cursor.getInt(cursor.getColumnIndex("Kcal"))
                fat = cursor.getDouble(cursor.getColumnIndex("Fat"))
                protein = cursor.getDouble(cursor.getColumnIndex("Protein"))
                carbo = cursor.getDouble(cursor.getColumnIndex("Carbohydrates"))
                weight = cursor.getInt(cursor.getColumnIndex("Weight"))
                favorite = cursor.getInt(cursor.getColumnIndex("Favorite"))
                id = cursor.getInt(cursor.getColumnIndex("ID_pro"))

                productList.add(Product(name,kcal,protein,carbo,fat,weight,favorite, id))

            }while (cursor.moveToNext())
        }

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

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_products_fragment, container, false)

        //read any product added to current day and calculate all nutrients
        val nutrientsSumArray = dbManager.readDayTable(date.toString())

        view.current_kcal.text = nutrientsSumArray[0].toInt().toString()
        view.current_pro.text = "%.1f".format(nutrientsSumArray[1]).replace(',','.')
        view.current_fat.text = "%.1f".format(nutrientsSumArray[2]).replace(',','.')
        view.current_carbo.text = "%.1f".format(nutrientsSumArray[3]).replace(',','.')

        //show dialog in you can add new product to data base
        view.floatingActionButton.setOnClickListener { addNewProductDialog() }

        //searching product recycle list view
        view.searchView.setOnQueryTextListener(this)

        view.products_recycle_view.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapterProduct = MyAdapter()
            adapter = adapterProduct
        }

        adapterProduct.submitList(productList)

        val kcalUserPref = view.kcal_user_pref
        val fatUserPref = view.fat_uset_pref
        val proUserPref = view.pro_user_pref
        val carboUserPref = view.carbo_user_pref

        view.settings_nutrients.setOnClickListener{
            nutrientsSettingsDialog(kcalUserPref, fatUserPref, proUserPref,carboUserPref)
        }

        readData(kcalUserPref,fatUserPref,proUserPref,carboUserPref)

        return view
    }

    fun nutrientsSettingsDialog(
        kcalUserPref: TextView,
        fatUserPref: TextView,
        proUserPref: TextView,
        carboUserPref: TextView
    ) {
        val nutrientsSettingsDialog = Dialog(requireContext())
        nutrientsSettingsDialog.setContentView(R.layout.nutrients_settings_dialog)
        nutrientsSettingsDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var kcal = nutrientsSettingsDialog.findViewById<EditText>(R.id.kcal_product_settings_dialog)
        var carbo = nutrientsSettingsDialog.findViewById<EditText>(R.id.carbo_user_product_settings_dialog)
        var fat = nutrientsSettingsDialog.findViewById<EditText>(R.id.fat_user_product_settings_dialog)
        var pro = nutrientsSettingsDialog.findViewById<EditText>(R.id.pro_user_product_settings_dialog)

        val readPref = requireContext().getSharedPreferences(MAIN_PREF,0)
        kcal.setText("${readPref.getInt(PREF_KCAL,0)}")
        carbo.setText("${readPref.getInt(PREF_CARBO,0)}")
        fat.setText("${readPref.getInt(PREF_FAT,0)}")
        pro.setText("${readPref.getInt(PREF_PRO,0)}")


        val saveBt = nutrientsSettingsDialog.findViewById<Button>(R.id.ok_product_settings_dialog)
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

            nutrientsSettingsDialog.let {
                it.dismiss()
                readData(kcalUserPref,fatUserPref,proUserPref,carboUserPref)
            }
        }

        nutrientsSettingsDialog.show()
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

    }
    private fun addNewProductDialog(){

        this.onPause()

        val myDataBase: MyDatabaseHelper = MyDatabaseHelper(requireContext())

        var addDialog: Dialog = Dialog(requireContext())

        var exist:Boolean = false

        addDialog.setContentView(R.layout.add_dialog_layout)
        addDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var name_new_product = addDialog.name_new_product
        var kcal_new_product = addDialog.kcal_new_product
        var protein_new_product = addDialog.protein_new_product
        var carbo_new_product = addDialog.carbo_new_product
        var fat_new_product = addDialog.fa_new_product
        var weight_new_product = addDialog.weight_new_product
        var favorite_chaek: CheckBox = addDialog.favorite_new_product
        val register = addDialog.register_new_product
        val cancel = addDialog.cancel_new_product

        register.setOnClickListener {

            if (name_new_product.length() == 0) name_new_product.setText("Item")
            if (kcal_new_product.length() == 0) kcal_new_product.setText("0")
            if (protein_new_product.length() == 0) protein_new_product.setText("0")
            if (carbo_new_product.length() == 0) carbo_new_product.setText("0")
            if (fat_new_product.length() == 0) fat_new_product.setText("0")
            if (weight_new_product.length() == 0) weight_new_product.setText("0")

            val newProduct = Product(
                name_new_product.text.toString().capitalize(),
                kcal_new_product.text.toString().toInt(),
                protein_new_product.text.toString().toDouble(),
                carbo_new_product.text.toString().toDouble(),
                fat_new_product.text.toString().toDouble(),
                weight_new_product.text.toString().toInt(),
                if (favorite_chaek.isChecked) 1 else 0,
                0
            )

            for (product in productList) {
                if (newProduct.name.equals(product.name)){
                    exist = true
                    break
                }else{
                    exist = false
                }
            }

            if (exist){
                Toast.makeText(requireContext(),"Already exist!",Toast.LENGTH_SHORT).show()
            }else if (!exist){
                myDataBase.insertData(newProduct).let { it -> if (it) addDialog.dismiss();productList.let {  it.add(newProduct); it.groupBy { it.name }}; refreshFragment(); onStart()}

            }
        }

        //add function to cancel Button
        cancel.setOnClickListener {v -> addDialog.dismiss() }

        addDialog.show()

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        adapterProduct.filter.filter(newText)

        return true
    }

    fun refreshFragment(){

        adapterProduct!!.notifyDataSetChanged()
    }
}
